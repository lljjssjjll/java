package collection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Practice_HashSet {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 중복 없는 집합 / 빠른 존재 여부 확인
    static void demonstratePurpose() {
        System.out.println("=== [용도] HashSet ===");

        HashSet<String> set = new HashSet<>();
        set.add("banana"); set.add("apple"); set.add("cherry");
        boolean added = set.add("apple"); // 중복 → false
        System.out.println("집합 구성           : " + set);
        System.out.println("중복 추가(apple)    : " + added + "  (이미 존재)");
        System.out.println("contains(banana)    : " + set.contains("banana"));
        System.out.println("contains(grape)     : " + set.contains("grape"));

        // 중복 제거 활용
        List<String> withDups = List.of("a", "b", "a", "c", "b", "d");
        HashSet<String> deduped = new HashSet<>(withDups);
        System.out.println("중복 포함 리스트    : " + withDups);
        System.out.println("중복 제거 결과      : " + deduped);
        System.out.println();
    }

    // 장점: add/contains/remove 평균 O(1) — 해시 함수로 버킷 직접 접근
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] HashSet ===");
        HashSet<Integer> set = new HashSet<>(SIZE * 2);
        for (int i = 0; i < SIZE; i++) set.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) set.contains(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("contains() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1) 평균)");
        System.out.println();
    }

    // 단점: 순서 보장 없음 / 해시 충돌 시 버킷이 LinkedList·TreeNode로 늘어나 O(n)·O(log n) 저하
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] HashSet ===");

        HashSet<String> set = new HashSet<>();
        List<String> inserted = List.of("banana", "apple", "cherry", "date", "elderberry");
        set.addAll(inserted);
        System.out.println("삽입 순서           : " + inserted);
        System.out.println("HashSet 순회 순서   : " + set);
        System.out.println("→ 삽입 순서 보장 없음 (해시 버킷 위치에 따라 결정)");

        // 동일 hashCode를 가진 키들은 같은 버킷에 쌓여 성능 저하
        // Java String에서 hashCode() 충돌 예시
        System.out.println("hashCode(\"Aa\") == hashCode(\"BB\") : " + ("Aa".hashCode() == "BB".hashCode()));
        System.out.println("→ 충돌 키가 많으면 버킷 내 탐색 비용 증가");
        System.out.println();
    }

    // 성능: vs TreeSet — contains 속도 (HashSet O(1) vs TreeSet O(log n))
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] HashSet vs TreeSet ===");
        Options opt = new OptionsBuilder()
                .include("Practice_HashSet\\.PerformanceDemonstration")
                .warmupIterations(2)
                .measurementIterations(3)
                .warmupTime(TimeValue.seconds(1))
                .measurementTime(TimeValue.seconds(1))
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @State(Scope.Thread)
    public static class PerformanceDemonstration {
        HashSet<Integer> hashSet;
        TreeSet<Integer> treeSet;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            hashSet = new HashSet<>(SIZE * 2);
            treeSet = new TreeSet<>();
            for (int i = 0; i < SIZE; i++) { hashSet.add(i); treeSet.add(i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public boolean hashSet_contains() {
            return hashSet.contains(idx++ % SIZE);
        }

        @Benchmark
        public boolean treeSet_contains() {
            return treeSet.contains(idx++ % SIZE);
        }

        @Benchmark
        public boolean hashSet_add() {
            return hashSet.add(idx++ % SIZE);
        }

        @Benchmark
        public boolean treeSet_add() {
            return treeSet.add(idx++ % SIZE);
        }
    }
}
