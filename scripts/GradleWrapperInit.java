///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 21

import static java.lang.System.*;

import java.io.File;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Arrays; 
import java.io.IOException;
import java.io.FileReader; 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.PosixFilePermission;

public class GradleWrapperInit {


    public static String gradlewSh = "IyEvYmluL3NoCgojCiMgQ29weXJpZ2h0IMKpIDIwMTUtMjAyMSB0aGUgb3JpZ2luYWwgYXV0aG9ycy4KIwojIExpY2Vuc2VkIHVuZGVyIHRoZSBBcGFjaGUgTGljZW5zZSwgVmVyc2lvbiAyLjAgKHRoZSAiTGljZW5zZSIpOwojIHlvdSBtYXkgbm90IHVzZSB0aGlzIGZpbGUgZXhjZXB0IGluIGNvbXBsaWFuY2Ugd2l0aCB0aGUgTGljZW5zZS4KIyBZb3UgbWF5IG9idGFpbiBhIGNvcHkgb2YgdGhlIExpY2Vuc2UgYXQKIwojICAgICAgaHR0cHM6Ly93d3cuYXBhY2hlLm9yZy9saWNlbnNlcy9MSUNFTlNFLTIuMAojCiMgVW5sZXNzIHJlcXVpcmVkIGJ5IGFwcGxpY2FibGUgbGF3IG9yIGFncmVlZCB0byBpbiB3cml0aW5nLCBzb2Z0d2FyZQojIGRpc3RyaWJ1dGVkIHVuZGVyIHRoZSBMaWNlbnNlIGlzIGRpc3RyaWJ1dGVkIG9uIGFuICJBUyBJUyIgQkFTSVMsCiMgV0lUSE9VVCBXQVJSQU5USUVTIE9SIENPTkRJVElPTlMgT0YgQU5ZIEtJTkQsIGVpdGhlciBleHByZXNzIG9yIGltcGxpZWQuCiMgU2VlIHRoZSBMaWNlbnNlIGZvciB0aGUgc3BlY2lmaWMgbGFuZ3VhZ2UgZ292ZXJuaW5nIHBlcm1pc3Npb25zIGFuZAojIGxpbWl0YXRpb25zIHVuZGVyIHRoZSBMaWNlbnNlLgojCiMgU1BEWC1MaWNlbnNlLUlkZW50aWZpZXI6IEFwYWNoZS0yLjAKIwoKIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjCiMKIyAgIEdyYWRsZSBzdGFydCB1cCBzY3JpcHQgZm9yIFBPU0lYIGdlbmVyYXRlZCBieSBHcmFkbGUuCiMKIyAgIEltcG9ydGFudCBmb3IgcnVubmluZzoKIwojICAgKDEpIFlvdSBuZWVkIGEgUE9TSVgtY29tcGxpYW50IHNoZWxsIHRvIHJ1biB0aGlzIHNjcmlwdC4gSWYgeW91ciAvYmluL3NoIGlzCiMgICAgICAgbm9uY29tcGxpYW50LCBidXQgeW91IGhhdmUgc29tZSBvdGhlciBjb21wbGlhbnQgc2hlbGwgc3VjaCBhcyBrc2ggb3IKIyAgICAgICBiYXNoLCB0aGVuIHRvIHJ1biB0aGlzIHNjcmlwdCwgdHlwZSB0aGF0IHNoZWxsIG5hbWUgYmVmb3JlIHRoZSB3aG9sZQojICAgICAgIGNvbW1hbmQgbGluZSwgbGlrZToKIwojICAgICAgICAgICBrc2ggR3JhZGxlCiMKIyAgICAgICBCdXN5Ym94IGFuZCBzaW1pbGFyIHJlZHVjZWQgc2hlbGxzIHdpbGwgTk9UIHdvcmssIGJlY2F1c2UgdGhpcyBzY3JpcHQKIyAgICAgICByZXF1aXJlcyBhbGwgb2YgdGhlc2UgUE9TSVggc2hlbGwgZmVhdHVyZXM6CiMgICAgICAgICAqIGZ1bmN0aW9uczsKIyAgICAgICAgICogZXhwYW5zaW9ucyDCqyR2YXLCuywgwqske3Zhcn3Cuywgwqske3ZhcjotZGVmYXVsdH3Cuywgwqske3ZhcitTRVR9wrssCiMgICAgICAgICAgIMKrJHt2YXIjcHJlZml4fcK7LCDCqyR7dmFyJXN1ZmZpeH3CuywgYW5kIMKrJCggY21kICnCuzsKIyAgICAgICAgICogY29tcG91bmQgY29tbWFuZHMgaGF2aW5nIGEgdGVzdGFibGUgZXhpdCBzdGF0dXMsIGVzcGVjaWFsbHkgwqtjYXNlwrs7CiMgICAgICAgICAqIHZhcmlvdXMgYnVpbHQtaW4gY29tbWFuZHMgaW5jbHVkaW5nIMKrY29tbWFuZMK7LCDCq3NldMK7LCBhbmQgwqt1bGltaXTCuy4KIwojICAgSW1wb3J0YW50IGZvciBwYXRjaGluZzoKIwojICAgKDIpIFRoaXMgc2NyaXB0IHRhcmdldHMgYW55IFBPU0lYIHNoZWxsLCBzbyBpdCBhdm9pZHMgZXh0ZW5zaW9ucyBwcm92aWRlZAojICAgICAgIGJ5IEJhc2gsIEtzaCwgZXRjOyBpbiBwYXJ0aWN1bGFyIGFycmF5cyBhcmUgYXZvaWRlZC4KIwojICAgICAgIFRoZSAidHJhZGl0aW9uYWwiIHByYWN0aWNlIG9mIHBhY2tpbmcgbXVsdGlwbGUgcGFyYW1ldGVycyBpbnRvIGEKIyAgICAgICBzcGFjZS1zZXBhcmF0ZWQgc3RyaW5nIGlzIGEgd2VsbCBkb2N1bWVudGVkIHNvdXJjZSBvZiBidWdzIGFuZCBzZWN1cml0eQojICAgICAgIHByb2JsZW1zLCBzbyB0aGlzIGlzIChtb3N0bHkpIGF2b2lkZWQsIGJ5IHByb2dyZXNzaXZlbHkgYWNjdW11bGF0aW5nCiMgICAgICAgb3B0aW9ucyBpbiAiJEAiLCBhbmQgZXZlbnR1YWxseSBwYXNzaW5nIHRoYXQgdG8gSmF2YS4KIwojICAgICAgIFdoZXJlIHRoZSBpbmhlcml0ZWQgZW52aXJvbm1lbnQgdmFyaWFibGVzIChERUZBVUxUX0pWTV9PUFRTLCBKQVZBX09QVFMsCiMgICAgICAgYW5kIEdSQURMRV9PUFRTKSByZWx5IG9uIHdvcmQtc3BsaXR0aW5nLCB0aGlzIGlzIHBlcmZvcm1lZCBleHBsaWNpdGx5OwojICAgICAgIHNlZSB0aGUgaW4tbGluZSBjb21tZW50cyBmb3IgZGV0YWlscy4KIwojICAgICAgIFRoZXJlIGFyZSB0d2Vha3MgZm9yIHNwZWNpZmljIG9wZXJhdGluZyBzeXN0ZW1zIHN1Y2ggYXMgQUlYLCBDeWdXaW4sCiMgICAgICAgRGFyd2luLCBNaW5HVywgYW5kIE5vblN0b3AuCiMKIyAgICgzKSBUaGlzIHNjcmlwdCBpcyBnZW5lcmF0ZWQgZnJvbSB0aGUgR3Jvb3Z5IHRlbXBsYXRlCiMgICAgICAgaHR0cHM6Ly9naXRodWIuY29tL2dyYWRsZS9ncmFkbGUvYmxvYi9IRUFEL3BsYXRmb3Jtcy9qdm0vcGx1Z2lucy1hcHBsaWNhdGlvbi9zcmMvbWFpbi9yZXNvdXJjZXMvb3JnL2dyYWRsZS9hcGkvaW50ZXJuYWwvcGx1Z2lucy91bml4U3RhcnRTY3JpcHQudHh0CiMgICAgICAgd2l0aGluIHRoZSBHcmFkbGUgcHJvamVjdC4KIwojICAgICAgIFlvdSBjYW4gZmluZCBHcmFkbGUgYXQgaHR0cHM6Ly9naXRodWIuY29tL2dyYWRsZS9ncmFkbGUvLgojCiMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIwoKIyBBdHRlbXB0IHRvIHNldCBBUFBfSE9NRQoKIyBSZXNvbHZlIGxpbmtzOiAkMCBtYXkgYmUgYSBsaW5rCmFwcF9wYXRoPSQwCgojIE5lZWQgdGhpcyBmb3IgZGFpc3ktY2hhaW5lZCBzeW1saW5rcy4Kd2hpbGUKICAgIEFQUF9IT01FPSR7YXBwX3BhdGglIiR7YXBwX3BhdGgjIyovfSJ9ICAjIGxlYXZlcyBhIHRyYWlsaW5nIC87IGVtcHR5IGlmIG5vIGxlYWRpbmcgcGF0aAogICAgWyAtaCAiJGFwcF9wYXRoIiBdCmRvCiAgICBscz0kKCBscyAtbGQgIiRhcHBfcGF0aCIgKQogICAgbGluaz0ke2xzIyonIC0+ICd9CiAgICBjYXNlICRsaW5rIGluICAgICAgICAgICAgICMoCiAgICAgIC8qKSAgIGFwcF9wYXRoPSRsaW5rIDs7ICMoCiAgICAgICopICAgIGFwcF9wYXRoPSRBUFBfSE9NRSRsaW5rIDs7CiAgICBlc2FjCmRvbmUKCiMgVGhpcyBpcyBub3JtYWxseSB1bnVzZWQKIyBzaGVsbGNoZWNrIGRpc2FibGU9U0MyMDM0CkFQUF9CQVNFX05BTUU9JHswIyMqL30KIyBEaXNjYXJkIGNkIHN0YW5kYXJkIG91dHB1dCBpbiBjYXNlICRDRFBBVEggaXMgc2V0IChodHRwczovL2dpdGh1Yi5jb20vZ3JhZGxlL2dyYWRsZS9pc3N1ZXMvMjUwMzYpCkFQUF9IT01FPSQoIGNkIC1QICIke0FQUF9IT01FOi0uL30iID4gL2Rldi9udWxsICYmIHByaW50ZiAnJXMKJyAiJFBXRCIgKSB8fCBleGl0CgojIFVzZSB0aGUgbWF4aW11bSBhdmFpbGFibGUsIG9yIHNldCBNQVhfRkQgIT0gLTEgdG8gdXNlIHRoYXQgdmFsdWUuCk1BWF9GRD1tYXhpbXVtCgp3YXJuICgpIHsKICAgIGVjaG8gIiQqIgp9ID4mMgoKZGllICgpIHsKICAgIGVjaG8KICAgIGVjaG8gIiQqIgogICAgZWNobwogICAgZXhpdCAxCn0gPiYyCgojIE9TIHNwZWNpZmljIHN1cHBvcnQgKG11c3QgYmUgJ3RydWUnIG9yICdmYWxzZScpLgpjeWd3aW49ZmFsc2UKbXN5cz1mYWxzZQpkYXJ3aW49ZmFsc2UKbm9uc3RvcD1mYWxzZQpjYXNlICIkKCB1bmFtZSApIiBpbiAgICAgICAgICAgICAgICAjKAogIENZR1dJTiogKSAgICAgICAgIGN5Z3dpbj10cnVlICA7OyAjKAogIERhcndpbiogKSAgICAgICAgIGRhcndpbj10cnVlICA7OyAjKAogIE1TWVMqIHwgTUlOR1cqICkgIG1zeXM9dHJ1ZSAgICA7OyAjKAogIE5PTlNUT1AqICkgICAgICAgIG5vbnN0b3A9dHJ1ZSA7Owplc2FjCgpDTEFTU1BBVEg9JEFQUF9IT01FL2dyYWRsZS93cmFwcGVyL2dyYWRsZS13cmFwcGVyLmphcgoKCiMgRGV0ZXJtaW5lIHRoZSBKYXZhIGNvbW1hbmQgdG8gdXNlIHRvIHN0YXJ0IHRoZSBKVk0uCmlmIFsgLW4gIiRKQVZBX0hPTUUiIF0gOyB0aGVuCiAgICBpZiBbIC14ICIkSkFWQV9IT01FL2pyZS9zaC9qYXZhIiBdIDsgdGhlbgogICAgICAgICMgSUJNJ3MgSkRLIG9uIEFJWCB1c2VzIHN0cmFuZ2UgbG9jYXRpb25zIGZvciB0aGUgZXhlY3V0YWJsZXMKICAgICAgICBKQVZBQ01EPSRKQVZBX0hPTUUvanJlL3NoL2phdmEKICAgIGVsc2UKICAgICAgICBKQVZBQ01EPSRKQVZBX0hPTUUvYmluL2phdmEKICAgIGZpCiAgICBpZiBbICEgLXggIiRKQVZBQ01EIiBdIDsgdGhlbgogICAgICAgIGRpZSAiRVJST1I6IEpBVkFfSE9NRSBpcyBzZXQgdG8gYW4gaW52YWxpZCBkaXJlY3Rvcnk6ICRKQVZBX0hPTUUKClBsZWFzZSBzZXQgdGhlIEpBVkFfSE9NRSB2YXJpYWJsZSBpbiB5b3VyIGVudmlyb25tZW50IHRvIG1hdGNoIHRoZQpsb2NhdGlvbiBvZiB5b3VyIEphdmEgaW5zdGFsbGF0aW9uLiIKICAgIGZpCmVsc2UKICAgIEpBVkFDTUQ9amF2YQogICAgaWYgISBjb21tYW5kIC12IGphdmEgPi9kZXYvbnVsbCAyPiYxCiAgICB0aGVuCiAgICAgICAgZGllICJFUlJPUjogSkFWQV9IT01FIGlzIG5vdCBzZXQgYW5kIG5vICdqYXZhJyBjb21tYW5kIGNvdWxkIGJlIGZvdW5kIGluIHlvdXIgUEFUSC4KClBsZWFzZSBzZXQgdGhlIEpBVkFfSE9NRSB2YXJpYWJsZSBpbiB5b3VyIGVudmlyb25tZW50IHRvIG1hdGNoIHRoZQpsb2NhdGlvbiBvZiB5b3VyIEphdmEgaW5zdGFsbGF0aW9uLiIKICAgIGZpCmZpCgojIEluY3JlYXNlIHRoZSBtYXhpbXVtIGZpbGUgZGVzY3JpcHRvcnMgaWYgd2UgY2FuLgppZiAhICIkY3lnd2luIiAmJiAhICIkZGFyd2luIiAmJiAhICIkbm9uc3RvcCIgOyB0aGVuCiAgICBjYXNlICRNQVhfRkQgaW4gIygKICAgICAgbWF4KikKICAgICAgICAjIEluIFBPU0lYIHNoLCB1bGltaXQgLUggaXMgdW5kZWZpbmVkLiBUaGF0J3Mgd2h5IHRoZSByZXN1bHQgaXMgY2hlY2tlZCB0byBzZWUgaWYgaXQgd29ya2VkLgogICAgICAgICMgc2hlbGxjaGVjayBkaXNhYmxlPVNDMjAzOSxTQzMwNDUKICAgICAgICBNQVhfRkQ9JCggdWxpbWl0IC1IIC1uICkgfHwKICAgICAgICAgICAgd2FybiAiQ291bGQgbm90IHF1ZXJ5IG1heGltdW0gZmlsZSBkZXNjcmlwdG9yIGxpbWl0IgogICAgZXNhYwogICAgY2FzZSAkTUFYX0ZEIGluICAjKAogICAgICAnJyB8IHNvZnQpIDo7OyAjKAogICAgICAqKQogICAgICAgICMgSW4gUE9TSVggc2gsIHVsaW1pdCAtbiBpcyB1bmRlZmluZWQuIFRoYXQncyB3aHkgdGhlIHJlc3VsdCBpcyBjaGVja2VkIHRvIHNlZSBpZiBpdCB3b3JrZWQuCiAgICAgICAgIyBzaGVsbGNoZWNrIGRpc2FibGU9U0MyMDM5LFNDMzA0NQogICAgICAgIHVsaW1pdCAtbiAiJE1BWF9GRCIgfHwKICAgICAgICAgICAgd2FybiAiQ291bGQgbm90IHNldCBtYXhpbXVtIGZpbGUgZGVzY3JpcHRvciBsaW1pdCB0byAkTUFYX0ZEIgogICAgZXNhYwpmaQoKIyBDb2xsZWN0IGFsbCBhcmd1bWVudHMgZm9yIHRoZSBqYXZhIGNvbW1hbmQsIHN0YWNraW5nIGluIHJldmVyc2Ugb3JkZXI6CiMgICAqIGFyZ3MgZnJvbSB0aGUgY29tbWFuZCBsaW5lCiMgICAqIHRoZSBtYWluIGNsYXNzIG5hbWUKIyAgICogLWNsYXNzcGF0aAojICAgKiAtRC4uLmFwcG5hbWUgc2V0dGluZ3MKIyAgICogLS1tb2R1bGUtcGF0aCAob25seSBpZiBuZWVkZWQpCiMgICAqIERFRkFVTFRfSlZNX09QVFMsIEpBVkFfT1BUUywgYW5kIEdSQURMRV9PUFRTIGVudmlyb25tZW50IHZhcmlhYmxlcy4KCiMgRm9yIEN5Z3dpbiBvciBNU1lTLCBzd2l0Y2ggcGF0aHMgdG8gV2luZG93cyBmb3JtYXQgYmVmb3JlIHJ1bm5pbmcgamF2YQppZiAiJGN5Z3dpbiIgfHwgIiRtc3lzIiA7IHRoZW4KICAgIEFQUF9IT01FPSQoIGN5Z3BhdGggLS1wYXRoIC0tbWl4ZWQgIiRBUFBfSE9NRSIgKQogICAgQ0xBU1NQQVRIPSQoIGN5Z3BhdGggLS1wYXRoIC0tbWl4ZWQgIiRDTEFTU1BBVEgiICkKCiAgICBKQVZBQ01EPSQoIGN5Z3BhdGggLS11bml4ICIkSkFWQUNNRCIgKQoKICAgICMgTm93IGNvbnZlcnQgdGhlIGFyZ3VtZW50cyAtIGtsdWRnZSB0byBsaW1pdCBvdXJzZWx2ZXMgdG8gL2Jpbi9zaAogICAgZm9yIGFyZyBkbwogICAgICAgIGlmCiAgICAgICAgICAgIGNhc2UgJGFyZyBpbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIygKICAgICAgICAgICAgICAtKikgICBmYWxzZSA7OyAgICAgICAgICAgICAgICAgICAgICAgICAgICAjIGRvbid0IG1lc3Mgd2l0aCBvcHRpb25zICMoCiAgICAgICAgICAgICAgLz8qKSAgdD0ke2FyZyMvfSB0PS8ke3QlJS8qfSAgICAgICAgICAgICAgIyBsb29rcyBsaWtlIGEgUE9TSVggZmlsZXBhdGgKICAgICAgICAgICAgICAgICAgICBbIC1lICIkdCIgXSA7OyAgICAgICAgICAgICAgICAgICAgICAjKAogICAgICAgICAgICAgICopICAgIGZhbHNlIDs7CiAgICAgICAgICAgIGVzYWMKICAgICAgICB0aGVuCiAgICAgICAgICAgIGFyZz0kKCBjeWdwYXRoIC0tcGF0aCAtLWlnbm9yZSAtLW1peGVkICIkYXJnIiApCiAgICAgICAgZmkKICAgICAgICAjIFJvbGwgdGhlIGFyZ3MgbGlzdCBhcm91bmQgZXhhY3RseSBhcyBtYW55IHRpbWVzIGFzIHRoZSBudW1iZXIgb2YKICAgICAgICAjIGFyZ3MsIHNvIGVhY2ggYXJnIHdpbmRzIHVwIGJhY2sgaW4gdGhlIHBvc2l0aW9uIHdoZXJlIGl0IHN0YXJ0ZWQsIGJ1dAogICAgICAgICMgcG9zc2libHkgbW9kaWZpZWQuCiAgICAgICAgIwogICAgICAgICMgTkI6IGEgYGZvcmAgbG9vcCBjYXB0dXJlcyBpdHMgaXRlcmF0aW9uIGxpc3QgYmVmb3JlIGl0IGJlZ2lucywgc28KICAgICAgICAjIGNoYW5naW5nIHRoZSBwb3NpdGlvbmFsIHBhcmFtZXRlcnMgaGVyZSBhZmZlY3RzIG5laXRoZXIgdGhlIG51bWJlciBvZgogICAgICAgICMgaXRlcmF0aW9ucywgbm9yIHRoZSB2YWx1ZXMgcHJlc2VudGVkIGluIGBhcmdgLgogICAgICAgIHNoaWZ0ICAgICAgICAgICAgICAgICAgICMgcmVtb3ZlIG9sZCBhcmcKICAgICAgICBzZXQgLS0gIiRAIiAiJGFyZyIgICAgICAjIHB1c2ggcmVwbGFjZW1lbnQgYXJnCiAgICBkb25lCmZpCgoKIyBBZGQgZGVmYXVsdCBKVk0gb3B0aW9ucyBoZXJlLiBZb3UgY2FuIGFsc28gdXNlIEpBVkFfT1BUUyBhbmQgR1JBRExFX09QVFMgdG8gcGFzcyBKVk0gb3B0aW9ucyB0byB0aGlzIHNjcmlwdC4KREVGQVVMVF9KVk1fT1BUUz0nIi1YbXg2NG0iICItWG1zNjRtIicKCiMgQ29sbGVjdCBhbGwgYXJndW1lbnRzIGZvciB0aGUgamF2YSBjb21tYW5kOgojICAgKiBERUZBVUxUX0pWTV9PUFRTLCBKQVZBX09QVFMsIEpBVkFfT1BUUywgYW5kIG9wdHNFbnZpcm9ubWVudFZhciBhcmUgbm90IGFsbG93ZWQgdG8gY29udGFpbiBzaGVsbCBmcmFnbWVudHMsCiMgICAgIGFuZCBhbnkgZW1iZWRkZWQgc2hlbGxuZXNzIHdpbGwgYmUgZXNjYXBlZC4KIyAgICogRm9yIGV4YW1wbGU6IEEgdXNlciBjYW5ub3QgZXhwZWN0ICR7SG9zdG5hbWV9IHRvIGJlIGV4cGFuZGVkLCBhcyBpdCBpcyBhbiBlbnZpcm9ubWVudCB2YXJpYWJsZSBhbmQgd2lsbCBiZQojICAgICB0cmVhdGVkIGFzICcke0hvc3RuYW1lfScgaXRzZWxmIG9uIHRoZSBjb21tYW5kIGxpbmUuCgpzZXQgLS0gXAogICAgICAgICItRG9yZy5ncmFkbGUuYXBwbmFtZT0kQVBQX0JBU0VfTkFNRSIgXAogICAgICAgIC1jbGFzc3BhdGggIiRDTEFTU1BBVEgiIFwKICAgICAgICBvcmcuZ3JhZGxlLndyYXBwZXIuR3JhZGxlV3JhcHBlck1haW4gXAogICAgICAgICIkQCIKCiMgU3RvcCB3aGVuICJ4YXJncyIgaXMgbm90IGF2YWlsYWJsZS4KaWYgISBjb21tYW5kIC12IHhhcmdzID4vZGV2L251bGwgMj4mMQp0aGVuCiAgICBkaWUgInhhcmdzIGlzIG5vdCBhdmFpbGFibGUiCmZpCgojIFVzZSAieGFyZ3MiIHRvIHBhcnNlIHF1b3RlZCBhcmdzLgojCiMgV2l0aCAtbjEgaXQgb3V0cHV0cyBvbmUgYXJnIHBlciBsaW5lLCB3aXRoIHRoZSBxdW90ZXMgYW5kIGJhY2tzbGFzaGVzIHJlbW92ZWQuCiMKIyBJbiBCYXNoIHdlIGNvdWxkIHNpbXBseSBnbzoKIwojICAgcmVhZGFycmF5IEFSR1MgPCA8KCB4YXJncyAtbjEgPDw8IiR2YXIiICkgJiYKIyAgIHNldCAtLSAiJHtBUkdTW0BdfSIgIiRAIgojCiMgYnV0IFBPU0lYIHNoZWxsIGhhcyBuZWl0aGVyIGFycmF5cyBub3IgY29tbWFuZCBzdWJzdGl0dXRpb24sIHNvIGluc3RlYWQgd2UKIyBwb3N0LXByb2Nlc3MgZWFjaCBhcmcgKGFzIGEgbGluZSBvZiBpbnB1dCB0byBzZWQpIHRvIGJhY2tzbGFzaC1lc2NhcGUgYW55CiMgY2hhcmFjdGVyIHRoYXQgbWlnaHQgYmUgYSBzaGVsbCBtZXRhY2hhcmFjdGVyLCB0aGVuIHVzZSBldmFsIHRvIHJldmVyc2UKIyB0aGF0IHByb2Nlc3MgKHdoaWxlIG1haW50YWluaW5nIHRoZSBzZXBhcmF0aW9uIGJldHdlZW4gYXJndW1lbnRzKSwgYW5kIHdyYXAKIyB0aGUgd2hvbGUgdGhpbmcgdXAgYXMgYSBzaW5nbGUgInNldCIgc3RhdGVtZW50LgojCiMgVGhpcyB3aWxsIG9mIGNvdXJzZSBicmVhayBpZiBhbnkgb2YgdGhlc2UgdmFyaWFibGVzIGNvbnRhaW5zIGEgbmV3bGluZSBvcgojIGFuIHVubWF0Y2hlZCBxdW90ZS4KIwoKZXZhbCAic2V0IC0tICQoCiAgICAgICAgcHJpbnRmICclc1xuJyAiJERFRkFVTFRfSlZNX09QVFMgJEpBVkFfT1BUUyAkR1JBRExFX09QVFMiIHwKICAgICAgICB4YXJncyAtbjEgfAogICAgICAgIHNlZCAnIHN+W14tWzphbG51bTpdKywuLzo9QF9dflxcJn5nOyAnIHwKICAgICAgICB0ciAnXG4nICcgJwogICAgKSIgJyIkQCInCgpleGVjICIkSkFWQUNNRCIgIiRAIgo=";
    public static String gradlewBt = "QHJlbQ0KQHJlbSBDb3B5cmlnaHQgMjAxNSB0aGUgb3JpZ2luYWwgYXV0aG9yIG9yIGF1dGhvcnMuDQpAcmVtDQpAcmVtIExpY2Vuc2VkIHVuZGVyIHRoZSBBcGFjaGUgTGljZW5zZSwgVmVyc2lvbiAyLjAgKHRoZSAiTGljZW5zZSIpOw0KQHJlbSB5b3UgbWF5IG5vdCB1c2UgdGhpcyBmaWxlIGV4Y2VwdCBpbiBjb21wbGlhbmNlIHdpdGggdGhlIExpY2Vuc2UuDQpAcmVtIFlvdSBtYXkgb2J0YWluIGEgY29weSBvZiB0aGUgTGljZW5zZSBhdA0KQHJlbQ0KQHJlbSAgICAgIGh0dHBzOi8vd3d3LmFwYWNoZS5vcmcvbGljZW5zZXMvTElDRU5TRS0yLjANCkByZW0NCkByZW0gVW5sZXNzIHJlcXVpcmVkIGJ5IGFwcGxpY2FibGUgbGF3IG9yIGFncmVlZCB0byBpbiB3cml0aW5nLCBzb2Z0d2FyZQ0KQHJlbSBkaXN0cmlidXRlZCB1bmRlciB0aGUgTGljZW5zZSBpcyBkaXN0cmlidXRlZCBvbiBhbiAiQVMgSVMiIEJBU0lTLA0KQHJlbSBXSVRIT1VUIFdBUlJBTlRJRVMgT1IgQ09ORElUSU9OUyBPRiBBTlkgS0lORCwgZWl0aGVyIGV4cHJlc3Mgb3IgaW1wbGllZC4NCkByZW0gU2VlIHRoZSBMaWNlbnNlIGZvciB0aGUgc3BlY2lmaWMgbGFuZ3VhZ2UgZ292ZXJuaW5nIHBlcm1pc3Npb25zIGFuZA0KQHJlbSBsaW1pdGF0aW9ucyB1bmRlciB0aGUgTGljZW5zZS4NCkByZW0NCkByZW0gU1BEWC1MaWNlbnNlLUlkZW50aWZpZXI6IEFwYWNoZS0yLjANCkByZW0NCg0KQGlmICIlREVCVUclIj09IiIgQGVjaG8gb2ZmDQpAcmVtICMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjDQpAcmVtDQpAcmVtICBHcmFkbGUgc3RhcnR1cCBzY3JpcHQgZm9yIFdpbmRvd3MNCkByZW0NCkByZW0gIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMNCg0KQHJlbSBTZXQgbG9jYWwgc2NvcGUgZm9yIHRoZSB2YXJpYWJsZXMgd2l0aCB3aW5kb3dzIE5UIHNoZWxsDQppZiAiJU9TJSI9PSJXaW5kb3dzX05UIiBzZXRsb2NhbA0KDQpzZXQgRElSTkFNRT0lfmRwMA0KaWYgIiVESVJOQU1FJSI9PSIiIHNldCBESVJOQU1FPS4NCkByZW0gVGhpcyBpcyBub3JtYWxseSB1bnVzZWQNCnNldCBBUFBfQkFTRV9OQU1FPSV+bjANCnNldCBBUFBfSE9NRT0lRElSTkFNRSUNCg0KQHJlbSBSZXNvbHZlIGFueSAiLiIgYW5kICIuLiIgaW4gQVBQX0hPTUUgdG8gbWFrZSBpdCBzaG9ydGVyLg0KZm9yICUlaSBpbiAoIiVBUFBfSE9NRSUiKSBkbyBzZXQgQVBQX0hPTUU9JSV+ZmkNCg0KQHJlbSBBZGQgZGVmYXVsdCBKVk0gb3B0aW9ucyBoZXJlLiBZb3UgY2FuIGFsc28gdXNlIEpBVkFfT1BUUyBhbmQgR1JBRExFX09QVFMgdG8gcGFzcyBKVk0gb3B0aW9ucyB0byB0aGlzIHNjcmlwdC4NCnNldCBERUZBVUxUX0pWTV9PUFRTPSItWG14NjRtIiAiLVhtczY0bSINCg0KQHJlbSBGaW5kIGphdmEuZXhlDQppZiBkZWZpbmVkIEpBVkFfSE9NRSBnb3RvIGZpbmRKYXZhRnJvbUphdmFIb21lDQoNCnNldCBKQVZBX0VYRT1qYXZhLmV4ZQ0KJUpBVkFfRVhFJSAtdmVyc2lvbiA+TlVMIDI+JjENCmlmICVFUlJPUkxFVkVMJSBlcXUgMCBnb3RvIGV4ZWN1dGUNCg0KZWNoby4gMT4mMg0KZWNobyBFUlJPUjogSkFWQV9IT01FIGlzIG5vdCBzZXQgYW5kIG5vICdqYXZhJyBjb21tYW5kIGNvdWxkIGJlIGZvdW5kIGluIHlvdXIgUEFUSC4gMT4mMg0KZWNoby4gMT4mMg0KZWNobyBQbGVhc2Ugc2V0IHRoZSBKQVZBX0hPTUUgdmFyaWFibGUgaW4geW91ciBlbnZpcm9ubWVudCB0byBtYXRjaCB0aGUgMT4mMg0KZWNobyBsb2NhdGlvbiBvZiB5b3VyIEphdmEgaW5zdGFsbGF0aW9uLiAxPiYyDQoNCmdvdG8gZmFpbA0KDQo6ZmluZEphdmFGcm9tSmF2YUhvbWUNCnNldCBKQVZBX0hPTUU9JUpBVkFfSE9NRToiPSUNCnNldCBKQVZBX0VYRT0lSkFWQV9IT01FJS9iaW4vamF2YS5leGUNCg0KaWYgZXhpc3QgIiVKQVZBX0VYRSUiIGdvdG8gZXhlY3V0ZQ0KDQplY2hvLiAxPiYyDQplY2hvIEVSUk9SOiBKQVZBX0hPTUUgaXMgc2V0IHRvIGFuIGludmFsaWQgZGlyZWN0b3J5OiAlSkFWQV9IT01FJSAxPiYyDQplY2hvLiAxPiYyDQplY2hvIFBsZWFzZSBzZXQgdGhlIEpBVkFfSE9NRSB2YXJpYWJsZSBpbiB5b3VyIGVudmlyb25tZW50IHRvIG1hdGNoIHRoZSAxPiYyDQplY2hvIGxvY2F0aW9uIG9mIHlvdXIgSmF2YSBpbnN0YWxsYXRpb24uIDE+JjINCg0KZ290byBmYWlsDQoNCjpleGVjdXRlDQpAcmVtIFNldHVwIHRoZSBjb21tYW5kIGxpbmUNCg0Kc2V0IENMQVNTUEFUSD0lQVBQX0hPTUUlXGdyYWRsZVx3cmFwcGVyXGdyYWRsZS13cmFwcGVyLmphcg0KDQoNCkByZW0gRXhlY3V0ZSBHcmFkbGUNCiIlSkFWQV9FWEUlIiAlREVGQVVMVF9KVk1fT1BUUyUgJUpBVkFfT1BUUyUgJUdSQURMRV9PUFRTJSAiLURvcmcuZ3JhZGxlLmFwcG5hbWU9JUFQUF9CQVNFX05BTUUlIiAtY2xhc3NwYXRoICIlQ0xBU1NQQVRIJSIgb3JnLmdyYWRsZS53cmFwcGVyLkdyYWRsZVdyYXBwZXJNYWluICUqDQoNCjplbmQNCkByZW0gRW5kIGxvY2FsIHNjb3BlIGZvciB0aGUgdmFyaWFibGVzIHdpdGggd2luZG93cyBOVCBzaGVsbA0KaWYgJUVSUk9STEVWRUwlIGVxdSAwIGdvdG8gbWFpbkVuZA0KDQo6ZmFpbA0KcmVtIFNldCB2YXJpYWJsZSBHUkFETEVfRVhJVF9DT05TT0xFIGlmIHlvdSBuZWVkIHRoZSBfc2NyaXB0XyByZXR1cm4gY29kZSBpbnN0ZWFkIG9mDQpyZW0gdGhlIF9jbWQuZXhlIC9jXyByZXR1cm4gY29kZSENCnNldCBFWElUX0NPREU9JUVSUk9STEVWRUwlDQppZiAlRVhJVF9DT0RFJSBlcXUgMCBzZXQgRVhJVF9DT0RFPTENCmlmIG5vdCAiIj09IiVHUkFETEVfRVhJVF9DT05TT0xFJSIgZXhpdCAlRVhJVF9DT0RFJQ0KZXhpdCAvYiAlRVhJVF9DT0RFJQ0KDQo6bWFpbkVuZA0KaWYgIiVPUyUiPT0iV2luZG93c19OVCIgZW5kbG9jYWwNCg0KOm9tZWdhDQo=";


    public static void downloadToFile(String url, String dest) {
        try {
            java.net.URL website = new java.net.URL(url);
            java.nio.channels.ReadableByteChannel rbc = java.nio.channels.Channels.newChannel(website.openStream());
            java.io.FileOutputStream fos = new java.io.FileOutputStream(dest);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String path, String content, boolean append) {
        try (FileWriter fileWriter = new FileWriter(path, append)) {
            fileWriter.write(content);
        } catch (IOException e) {
            // exception handling ...
        }
    }

        
    public static void main(String... args) throws IOException{
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        
        java.io.File file = new java.io.File("gradle");
        if (file.isDirectory()) {
            out.println("Error: gradle is already installed.");
            exit(1);
        } else {
            out.println("Installing gradle..."); 

            {
                File wrapperDir = new java.io.File("gradle/wrapper");
                wrapperDir.mkdirs();
            }

            downloadToFile(
                "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar", 
                "gradle/wrapper/gradle-wrapper.jar");

            downloadToFile(
                "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.properties", 
                "gradle/wrapper/gradle-wrapper.properties");
            
            writeToFile(
                "gradlew", 
                new String(Base64.getDecoder().decode(gradlewSh), StandardCharsets.UTF_8),
                false
            );

            writeToFile(
                "gradlew.bat", 
                new String(Base64.getDecoder().decode(gradlewBt), StandardCharsets.UTF_8),
                false
            );

            if (!isWindows) {
                File gradlew = new File("gradlew");
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);
                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_EXECUTE);
                Files.setPosixFilePermissions(gradlew.toPath(), perms);
            }

            if (!isWindows) {
                String cmd = "./gradlew wrapper --gradle-version 8.9";
                out.println(cmd);
                Runtime.getRuntime().exec(cmd);
            } else {
                String cmd = "gradlew.bat wrapper --gradle-version 8.9";
                out.println(cmd);
                Runtime.getRuntime().exec("cmd /c " + cmd);
            }

            // Update Gradle to version 8.9
            {
                String gradleVersion = "8.9";
                List<String> commandList = new ArrayList<>();
        
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    commandList.add("gradlew.bat");
                    commandList.add("--no-daemon");
                } else {
                    commandList.add("./gradlew");
                    commandList.add("--no-daemon");
                }
                commandList.add("wrapper");
                commandList.add("--gradle-version");
                commandList.add(gradleVersion);
        
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(commandList);
                    processBuilder.directory(new java.io.File("."));
        
                    Process process = processBuilder.start();
        
                    // Read the output of the process
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    System.out.println("Gradle Output:");
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }

                    // Read any error output
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    System.out.println("\nGradle Error Output:");
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }

                    // Wait for the process to complete
                    int exitCode = process.waitFor();
                    System.out.println("\nGradle process finished with exit code: " + exitCode);

        
                } catch (IOException e) {
                    System.err.println("Error executing Gradle command: " + e.getMessage());
                } catch (InterruptedException e) {
                    System.err.println("Process interrupted: " + e.getMessage());
                }
            }

            out.println("Done.");
        }   
    }
}
