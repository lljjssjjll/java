package collection;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Practice_HashMap {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
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
    static void demonstratePerformance() {
        System.out.println("=== [성능] HashMap vs TreeMap ===");

        HashMap<Integer, Integer> hashMap = new HashMap<>(SIZE * 2);
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();

        // put 비교
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashMap.put(i, i);
        long hashPut = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) treeMap.put(i, i);
        long treePut = System.nanoTime() - start;

        System.out.printf("put() %,d회 → HashMap: %,d ns  TreeMap: %,d ns%n", SIZE, hashPut, treePut);

        // get 비교
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashMap.get(i);
        long hashGet = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) treeMap.get(i);
        long treeGet = System.nanoTime() - start;

        System.out.printf("get() %,d회 → HashMap: %,d ns  TreeMap: %,d ns%n", SIZE, hashGet, treeGet);
        System.out.println("→ TreeMap은 정렬 유지(Red-Black Tree)로 HashMap 대비 느리지만 범위 연산 지원");
        System.out.println();
    }
}
