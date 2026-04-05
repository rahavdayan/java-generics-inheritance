# Pair&lt;A, B&gt; — Java Generics & Inheritance Exercise

One file, four concepts, 24 incremental sub-phases.

## What this is

A single-file Java exercise (`PairDemo.java`) that builds up four ideas
one small step at a time, each with a deliberate compiler or runtime
error to make the concept concrete.

| Step | Concept | Key idea |
|------|---------|----------|
| 1 | **Generics** | `<A,B>` replaced at the call site — type errors before the JVM runs |
| 2 | **Inheritance** | `extends`, `super()`, inherited methods without redeclaration |
| 3 | **Polymorphism** | Declared type gates calls; actual object picks the implementation |
| 4 | **Invariance** | `Pair<String,Integer>` is not a `Pair<Object,Object>` |

## How to run

Requires only a JDK — no build tool needed.

```
javac PairDemo.java
java PairDemo
```

## Deliberate errors

Four lines are commented out. Each one was compiled on purpose to
produce a specific error message, then commented out before the next
step. To re-trigger an error, uncomment the line and recompile.

| Location | Error type | What it shows |
|----------|------------|---------------|
| Step 1.6 | Compile | Wrong type argument — generics catch it before runtime |
| Step 3.3 | Compile | `getLabel()` not on declared type `Pair` |
| Step 3.6 | Runtime | `ClassCastException` — bad downcast passes javac, fails at JVM |
| Step 4.1 | Compile | Invariance — `Pair<String,Integer>` not assignable to `Pair<Object,Object>` |
