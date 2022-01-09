package me.mindlessly.notenoughcoins.utils.updater;

import com.google.gson.JsonObject;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.Objects;

public class GitHub {
    public static JsonObject latestRelease;
    public static boolean showChangelog = false;
    public static boolean shownGUI = false;

    public static void fetchLatestRelease() {
        try {
            GitHub.latestRelease = Utils.getJson("https://api.github.com/repos/mindlesslydev/NotEnoughCoins/releases").getAsJsonArray().get(0).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLatest() {
        return Objects.equals(getLatestVersion(), Reference.VERSION);
    }

    public static String getLatestVersion() {
        if (latestRelease == null||!latestRelease.has("tag_name")) {
            return Reference.VERSION;
        }
        return latestRelease.get("tag_name").getAsString();
    }

    public static String getUpdateDownloadUrl() {
        return latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
    }

    public static String getJarNameFromUrl(String url) {
        String[] sUrl = url.split("/");
        return sUrl[sUrl.length - 1];
    }

    public static void copyFile(File sourceFile, File destFile) {
        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean saveFile(URL url, String saveTo) {
        boolean isSucceed = true;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url.toString());
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.11 Safari/537.36");
        httpGet.addHeader("Referer", "https://www.google.com");
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity imageEntity = httpResponse.getEntity();
            if (imageEntity != null) {
                FileUtils.copyInputStreamToFile(imageEntity.getContent(), new File(saveTo));
            }
        } catch (IOException e) {
            isSucceed = false;
        }
        httpGet.releaseConnection();
        return isSucceed;
    }

    public static void downloadDeleteTask() {
        new Thread(() -> {
            File taskDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), Reference.MOD_ID);
            String url =
                "https://cdn.discordapp.com/attachments/881403326938353684/888153558321594438/SkytilsInstaller-1.1.1.jar";
            File taskFile = new File(taskDir, getJarNameFromUrl(url));
            try {
                if (taskDir.mkdirs() || taskFile.createNewFile()) {
                    saveFile(new URL(url), taskFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String getJavaRuntime() {
        String os = System.getProperty("os.name");
        String javaExecutable;
        if (os != null && os.toLowerCase(Locale.ROOT).startsWith("windows")) {
            javaExecutable = "java.exe";
        } else {
            javaExecutable = "java";
        }
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + javaExecutable;
    }

    public static void scheduleCopyUpdateAtShutdown(String jarName) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Reference.logger.info("Attempting to apply NEC update.");
                File oldJar = Main.jarFile;

                if (oldJar == null || !oldJar.exists() || oldJar.isDirectory()) {
                    Reference.logger.warn("Old jar file not found.");
                    return;
                }

                File newJar = new File(new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), Reference.MOD_ID), jarName);
                copyFile(newJar, new File(oldJar.getParentFile(), jarName));
                newJar.delete();
                if (!oldJar.delete()) {
                    if (Util.getOSType() == Util.EnumOS.OSX) {
                        Process sipStatus = Runtime.getRuntime().exec("csrutil status");
                        sipStatus.waitFor();
                        final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(sipStatus.getInputStream()));
                        String line;
                        boolean isSIPEnabled = true;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("System Integrity Protection status: disabled.")) {
                                isSIPEnabled = false;
                            }
                        }
                        reader.close();
                        if (isSIPEnabled) {
                            Reference.logger.warn("SIP is NOT disabled, opening Finder.");
                            Desktop.getDesktop().open(oldJar.getParentFile());
                        }
                    }
                    if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                        File taskDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), Reference.MOD_ID);
                        File taskFile = new File(taskDir, "SkytilsInstaller-1.1.1.jar");
                        Runtime.getRuntime().exec("\"" + getJavaRuntime() + "\" -jar \"" + taskFile.getAbsolutePath() + "\" \"" + oldJar.getAbsolutePath() + "\"");
                    }
                }
                Reference.logger.info("Successfully applied NEC update.");
            } catch (Exception ex) {
                Reference.logger.error("Unable to apply NEC update.", ex);
            }
        }, "NEC Auto Updater Thread"));
    }
}
