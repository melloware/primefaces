declare namespace InheritParamObjectDestructure {
    interface Foo {
        /**
         * @param x param x
         * @param param1 object destructure 1
         * @param u param u
         * @param v param v
         * @param w1 param w1
         * @param w2 param computed
         * @param k param k
         */
        foo(x, { u, v, w: w1, [1 + 2]: w2, ...k }): void;
    }
    interface Bar extends Foo {
        /**
         * @inheritdoc
         * @param c param computed
         */
        foo(a, { w: b, u, [1 + 2]: c, v, ...d }): void;
    }
}