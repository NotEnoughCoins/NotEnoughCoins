# Minecraft Session Vulnerability POC 

**Severity**: Moderate

### Description
Minecraft session tokens if hijacked by a malicious mod or other means can be used by any user 

### Reproduction
Note - this is for educational purposes only and I highly advise against and do not condone session hijacking.

- Add a session stealer to your mod
- Load somebody's session ID with this mod
- Shazam, you are in!

### Resolution
Mojang need to add an IP check to their authentication. When the token is created, in Mojang's server it should have a reference of the user's IP address. Then, if the token is hijacked and the hacker attempts to use the token, due to having a different IP address it will not work.


