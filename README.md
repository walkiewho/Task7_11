# Simple cycle finder

A minimal Java Swing project that:
- finds all simple cycles in an undirected graph;
- ignores specified forbidden vertices;
- shows all found cycles in a window;
- highlights the selected cycle with a green-to-red gradient.

## Input format

```
N
M
u1 v1
u2 v2
...
uM vM
K
x1 x2 ... xK
```

Vertices are 0-based.

## Run

```bash
javac -d out $(find src -name '*.java')
java -cp out simplecycles.Main
```
