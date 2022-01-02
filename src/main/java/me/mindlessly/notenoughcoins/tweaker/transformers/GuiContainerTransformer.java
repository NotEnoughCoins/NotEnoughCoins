package me.mindlessly.notenoughcoins.tweaker.transformers;

import me.mindlessly.notenoughcoins.tweaker.utils.TransformerClass;
import me.mindlessly.notenoughcoins.tweaker.utils.TransformerMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class GuiContainerTransformer implements ITransformer {
    /**
     * {@link net.minecraft.client.gui.inventory.GuiContainer}
     */
    @Override
    public String[] getClassName() {
        return new String[]{TransformerClass.GuiContainer.getTransformerName()};
    }

    @Override
    public void transform(ClassNode classNode, String name) {

        for (MethodNode methodNode : classNode.methods) {
            if (TransformerMethod.drawScreen.matches(methodNode)) {
                // Objective:
                // Find: this.drawSlot(slot);
                // Add: GuiContainerHook.drawSlot(this, slot);

                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractNode = iterator.next();
                    if (abstractNode instanceof MethodInsnNode && abstractNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractNode;
                        if (methodInsnNode.owner.equals(TransformerClass.GuiContainer.getNameRaw()) && TransformerMethod.drawSlot.matches(methodInsnNode)) {
                            methodNode.instructions.insert(abstractNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "me/mindlessly/notenoughcoins/tweaker/hooks/GuiContainerHook",
                                "drawSlot", "(" + TransformerClass.GuiContainer.getName() + TransformerClass.Slot.getName() + ")V", false));

                            methodNode.instructions.insert(abstractNode, new VarInsnNode(Opcodes.ALOAD, 9)); // slot

                            methodNode.instructions.insert(abstractNode, new VarInsnNode(Opcodes.ALOAD, 0)); // this
                        }
                    }
                }
            }
        }
    }
}
