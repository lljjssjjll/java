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

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class Practice_HashMap {
    private static final int SIZE = 500_000;

    public static void main(String[] args) throws Exception {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 키-값 쌍 저장 / 빠른 키 조회·수정·삭제
    static void demonstratePurpose() {
        System.out.println("=== [용도] HashMap ===");

        HashMap<String, Integer> map = new HashMap<>();
        map.put("apple",  3);
        map.put("banana", 5);
        map.put("cherry", 2);
        System.out.println("초기 맵              : " + map);

        map.put("apple", 10);                    // 덮어쓰기
        System.out.println("apple 값 갱신        : " + map);

        System.out.println("get(banana)          : " + map.get("banana"));
        System.out.println("containsKey(cherry)  : " + map.containsKey("cherry"));
        System.out.println("containsValue(10)    : " + map.containsValue(10));
        System.out.println("getOrDefault(grape,0): " + map.getOrDefault("grape", 0));

        map.remove("cherry");
        System.out.println("remove(cherry) 후    : " + map);

        // 빈도 수집
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};
        HashMap<String, Integer> freq = new HashMap<>();
        for (String w : words) freq.merge(w, 1, Integer::sum);
        System.out.println("단어 빈도            : " + freq);
        System.out.println();
    }

    // 장점: get/put/containsKey/remove 평균 O(1) — 해시 함수로 버킷 직접 접근
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] HashMap ===");
        HashMap<Integer, Integer> map = new HashMap<>(SIZE * 2);
        for (int i = 0; i < SIZE; i++) map.put(i, i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) map.get(i);
        long getTime = System.nanoTime() - start;
        System.out.println("get() " + SIZE + "회 : " + getTime / 1_000_000 + " ms  (O(1) 평균)");

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) map.containsKey(i);
        long containsTime = System.nanoTime() - start;
        System.out.println("containsKey() " + SIZE + "회 : " + containsTime / 1_000_000 + " ms  (O(1) 평균)");
        System.out.println();
    }

    // 단점: 키 순서 보장 없음 / 해시 충돌 시 버킷 내 탐색 비용 증가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] HashMap ===");

        HashMap<String, Integer> map = new HashMap<>();
        map.put("banana", 2); map.put("apple", 1); map.put("cherry", 3);
        map.put("date", 4);   map.put("elderberry", 5);
        System.out.println("삽입 순서: banana → apple → cherry → date → elderberry");
        System.out.println("entrySet 순서: " + map.entrySet());
        System.out.println("→ 삽입 순서 미보장 (해시 버킷 위치에 따라 결정)");

        // 해시 충돌 예시
        System.out.println("hashCode(\"Aa\") == hashCode(\"BB\") : " + ("Aa".hashCode() == "BB".hashCode()));
        System.out.println("→ 충돌 키가 같은 버킷에 쌓이면 버킷 내 O(n) 또는 O(log n) 탐색");
        System.out.println();
    }

    // 성능: vs TreeMap — get/put 속도 (HashMap O(1) vs TreeMap O(log n))
    static void demonstratePerformance() throws Exception {
        System.out.println("=== [성능] HashMap vs TreeMap ===");
        Options opt = new OptionsBuilder()
                .include("Practice_HashMap\\.PerformanceDemonstration")
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
        HashMap<Integer, Integer> hashMap;
        TreeMap<Integer, Integer> treeMap;
        int idx;

        @Setup(Level.Trial)
        public void setup() {
            hashMap = new HashMap<>(SIZE * 2);
            treeMap = new TreeMap<>();
            for (int i = 0; i < SIZE; i++) { hashMap.put(i, i); treeMap.put(i, i); }
        }

        @Setup(Level.Iteration)
        public void resetIdx() { idx = 0; }

        @Benchmark
        public Integer hashMap_get() {
            return hashMap.get(idx++ % SIZE);
        }

        @Benchmark
        public Integer treeMap_get() {
            return treeMap.get(idx++ % SIZE);
        }

        @Benchmark
        public Integer hashMap_put() {
            int i = idx++ % SIZE;
            return hashMap.put(i, i);
        }

        @Benchmark
        public Integer treeMap_put() {
            int i = idx++ % SIZE;
            return treeMap.put(i, i);
        }
    }
}
