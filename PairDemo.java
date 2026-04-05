// =============================================================
// PairDemo.java  —  javac PairDemo.java  &&  java PairDemo
// =============================================================

// -----------------------------------------------------------------
// STEP 1 — Basic Generics
// -----------------------------------------------------------------

// <A, B> declares two type parameters.
// They are NOT types themselves — they are named placeholders.
// The compiler replaces them with real types at every call site.
class Pair<A, B> {
    // A and B here are the same placeholders as on the class header.
    // When the caller writes Pair<String, Integer>, the compiler reads
    // these as:  private final String first;  private final Integer second;
    private final A first;
    private final B second;

    // Parameters are typed A and B — the same placeholders.
    // The compiler checks at the call site that the arguments match,
    // then stores them here, satisfying the final-initialization requirement.
    public Pair(A first, B second) {
        this.first  = first;
        this.second = second;
    }
    // Return type is A (not Object) — the compiler tracks the substitution
    // all the way back to the call site. No cast, no guessing.
    public A getFirst()  { return first; }
    public B getSecond() { return second; }
    // Inside this class, A and B are treated as Object by the JVM (type erasure).
    // Concatenation still works because every Object has a toString() method.
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

// -----------------------------------------------------------------
// STEP 2 — Inheritance
// -----------------------------------------------------------------

// extends Pair<A, B> means two things:
//   1. LabeledPair IS-A Pair (subclass relationship)
//   2. The same A and B flow into Pair — not new parameters, the same ones
// When a caller writes LabeledPair<String, Integer>, Pair<String, Integer>
// is automatically satisfied as well.
class LabeledPair<A, B> extends Pair<A, B> {
    // Only the NEW state lives here. first and second belong to Pair — not re-declared.
    private final String label;

    // super(first, second) MUST be the first statement — Java requires it.
    // It calls Pair's constructor, which stores first and second.
    // LabeledPair never touches those fields directly; Pair owns them.
    public LabeledPair(String label, A first, B second) {
        super(first, second);
        this.label = label;
    }
    // getFirst() and getSecond() are inherited from Pair — no redeclaration needed.
    // Only getLabel() is new, because label is the only new state.
    public String getLabel() { return label; }
    // @Override tells the compiler: "this replaces Pair's toString()".
    // super.toString() reuses Pair's formatting — no duplication.
    @Override
    public String toString() {
        return label + ": " + super.toString();
    }
}

class PairDemo {
    public static void main(String[] args) {
        // =============================================================
        // FOUR CONCEPTS DEMONSTRATED IN THIS FILE
        //
        // 1. GENERICS — compile-time promise
        //    <A,B> are placeholders replaced at the call site.
        //    getFirst() returns A (not Object) — no cast, compiler-verified.
        //    Wrong type at the call site → error before the JVM runs anything.
        //
        // 2. INHERITANCE — extends, super(), inherited methods
        //    LabeledPair<A,B> extends Pair<A,B>.
        //    Pair owns first/second; LabeledPair only adds label.
        //    getFirst()/getSecond() are inherited — not redeclared.
        //
        // 3. POLYMORPHISM — declared type vs runtime type
        //    A LabeledPair stored as Pair: compiler sees Pair, JVM sees LabeledPair.
        //    Compiler decides what calls are legal (declared type).
        //    JVM decides which implementation runs (actual object).
        //
        // 4. INVARIANCE — type parameters must match exactly
        //    Pair<String,Integer> is NOT a Pair<Object,Object>.
        //    Class hierarchy is covariant; type parameters are invariant.
        //    Wildcard Pair<?,?> opts out of specific types — read-only safe.
        // =============================================================

        // ----- Step 1: Basic Generics -----

        // A=String, B=Integer — the compiler substitutes everywhere in Pair.
        Pair<String, Integer> p1 = new Pair<>("hello", 42);
        // A=String, B=String — totally different substitution, same class.
        Pair<String, String>  p2 = new Pair<>("hello", "world");

        System.out.println(p1.getFirst());   // "hello"  — String, no cast
        System.out.println(p1.getSecond());  // 42       — Integer, no cast
        System.out.println(p2.getFirst());   // "hello"
        System.out.println(p2.getSecond());  // "world"

        // ERROR: intentional — "oops" is a String, but B=Integer here.
        // The JVM never sees this line; javac rejects it before bytecode is generated.
        // Pair<String, Integer> p3 = new Pair<>("hello", "oops"); // ERROR: intentional — remove before next sub-phase

        // ----- Step 2: Inheritance -----

        LabeledPair<String, Integer> lp = new LabeledPair<>("score", "Alice", 99);

        System.out.println(lp.getFirst());   // "Alice"  — inherited from Pair, not redeclared
        System.out.println(lp.getLabel());   // "score"  — defined on LabeledPair
        System.out.println(lp);              // "score: (Alice, 99)" — LabeledPair's toString() chains to Pair's

        // ----- Step 3: Polymorphism -----

        // LabeledPair<String,Integer> IS-A Pair<String,Integer>, so this assignment is legal.
        // The object in memory is still a LabeledPair — nothing about it changed.
        // The declared type of p is Pair, which controls what the compiler lets you call.
        Pair<String, Integer> p = new LabeledPair<>("score", "Alice", 99);

        // Pair declares getFirst(), so the compiler allows this.
        // Return type is String — A=String is tracked through the declared type.
        System.out.println(p.getFirst());   // "Alice"

        // ERROR: intentional — Pair has no getLabel(). The compiler checks the
        // declared type only; it doesn't look at what p actually holds at runtime.
        // System.out.println(p.getLabel()); // ERROR: intentional — remove before next sub-phase

        // toString() is defined on Pair, so the compiler allows the call.
        // But the JVM dispatches to LabeledPair's version at runtime — the actual object wins.
        // Output: "score: (Alice, 99)"  NOT  "(Alice, 99)"
        System.out.println(p);

        // Downcast: you tell the compiler "this is actually a LabeledPair".
        // The compiler takes your word for it and unlocks getLabel().
        // If you're wrong about the runtime type, the JVM throws ClassCastException.
        String label = ((LabeledPair<String, Integer>) p).getLabel();
        System.out.println(label);   // "score"

        // ERROR: intentional — p2 holds a plain Pair, not a LabeledPair.
        // The compiler accepts the cast (it trusts you), but the JVM rejects it at runtime.
        Pair<String, String> plainPair = new Pair<>("x", "y");
        // String oops = ((LabeledPair<String, String>) plainPair).getLabel(); // ERROR: intentional — remove before next sub-phase

        // ----- Step 4: Invariance -----

        LabeledPair<String, Integer> lp2 = new LabeledPair<>("score", "Alice", 99);

        // ERROR: intentional — even though LabeledPair IS-A Pair, and String/Integer ARE Object,
        // Pair<String,Integer> is NOT a Pair<Object,Object>. Type parameters must match exactly.
        // Pair<Object, Object> wide = lp2; // ERROR: intentional — remove before next sub-phase

        // WHY does the compiler forbid it? Imagine Pair had a setter:
        //
        //   Pair<Object, Object> wide = lp2;  // hypothetically allowed
        //   wide.setFirst(42);                 // valid — 42 is an Object
        //   // but lp2.getFirst() now returns an Integer where String was promised — silent corruption
        //
        // The compiler can't know whether YOUR specific Pair has a setter.
        // So it applies the rule to all generic types uniformly: type parameters must match exactly.

        // Two subtype relationships — only one works:
        //
        //   Pair<String, Integer> x = lp2;         // OK   — class hierarchy: LabeledPair IS-A Pair
        //                                           //        type params are identical on both sides
        //
        //   Pair<Object, Object>  y = lp2;         // FAIL — type params differ: String != Object
        //                                           //        class hierarchy is irrelevant here
        //
        // Rule: widening across the CLASS hierarchy is fine as long as type params stay identical.
        //       Widening the TYPE PARAMS themselves is never allowed.
        Pair<String, Integer> x = lp2;   // compiles — same type params, class widening only

        // Wildcard: Pair<?,?> means "a Pair of some types — I don't know which".
        // Because no specific type is claimed, there's no corruption risk.
        // This assignment always compiles regardless of what lp2's type params are.
        Pair<?, ?> anyPair = lp2;   // compiles — wildcard accepts any type params

        // Reading is safe — the object is there, the JVM can hand it back.
        // But the return type is Object, not String — the compiler lost the specific type.
        Object first = anyPair.getFirst();   // Object, not String
        System.out.println(first);           // "Alice"

        // Writing would be blocked — the compiler can't verify the type is compatible.
        // anyPair.setFirst("hello");  // hypothetical — would be a compile error
    }
}
