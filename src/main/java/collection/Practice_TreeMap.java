package collection;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Practice_TreeMap {
    private static final int SIZE = 500_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 키 자동 정렬 맵 / 범위 검색·최솟값·최댓값 키 조회
    static void demonstratePurpose() {
        System.out.println("=== [용도] TreeMap ===");

        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(5, "E"); map.put(3, "C"); map.put(8, "H");
        map.put(1, "A"); map.put(9, "I"); map.put(2, "B");
        map.put(7, "G"); map.put(4, "D"); map.put(6, "F");
        System.out.println("삽입 순서: [5,3,8,1,9,2,7,4,6]");
        System.out.println("keySet()       : " + map.keySet()        + "  (자동 오름차순)");

        System.out.println("firstKey()     : " + map.firstKey()      + "  (최솟값 키)");
        System.out.println("lastKey()      : " + map.lastKey()       + "  (최댓값 키)");
        System.out.println("floorKey(6)    : " + map.floorKey(6)     + "  (6 이하 최댓값 키)");
        System.out.println("ceilingKey(6)  : " + map.ceilingKey(6)   + "  (6 이상 최솟값 키)");
        System.out.println("lowerKey(6)    : " + map.lowerKey(6)     + "  (6 미만 최댓값 키)");
        System.out.println("higherKey(6)   : " + map.higherKey(6)    + "  (6 초과 최솟값 키)");
        System.out.println("subMap(3,7)    : " + map.subMap(3, 7)    + "  (3 이상 7 미만)");
        System.out.println("headMap(5)     : " + map.headMap(5)      + "  (5 미만)");
        System.out.println("tailMap(5)     : " + map.tailMap(5)      + "  (5 이상)");

        NavigableMap<Integer, String> desc = map.descendingMap();
        System.out.println("descendingMap(): " + desc.keySet()       + "  (역순)");
        System.out.println();
    }

    // 장점: 항상 정렬 유지(Red-Black Tree) / 범위 뷰 subMap·headMap·tailMap 제공
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] TreeMap ===");
        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int i = SIZE; i >= 1; i--) map.put(i, i); // 역순 삽입

        System.out.println("역순 삽입 후 firstKey() : " + map.firstKey() + "  (항상 최솟값)");
        System.out.println("역순 삽입 후 lastKey()  : " + map.lastKey()  + "  (항상 최댓값)");

        Map<Integer, Integer> range = map.subMap(1000, 2000);
        System.out.println("subMap(1000,2000) 크기  : " + range.size() + "  (범위 뷰, 별도 복사 없음)");
        System.out.println();
    }

    // 단점: 모든 연산 O(log n) — HashMap O(1) 대비 느림 / null 키 불가
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] TreeMap ===");

        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int i = 0; i < SIZE; i++) map.put(i, i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) map.get(i);
        long elapsed = System.nanoTime() - start;
        System.out.println("get() " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(log n) 트리 탐색)");

        // null 키 불가
        try {
            map.put(null, -1);
        } catch (NullPointerException e) {
            System.out.println("null 키 삽입        : NullPointerException  (자연 순서 비교 불가)");
        }
        System.out.println();
    }

    // 성능: vs HashMap — put/get 속도 (HashMap O(1) vs TreeMap O(log n))
    static void demonstratePerformance() {
        System.out.println("=== [성능] TreeMap vs HashMap ===");

        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        HashMap<Integer, Integer> hashMap = new HashMap<>(SIZE * 2);

        // put 비교
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) treeMap.put(i, i);
        long treePut = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashMap.put(i, i);
        long hashPut = System.nanoTime() - start;

        System.out.printf("put() %,d회 → TreeMap: %,d ns  HashMap: %,d ns%n", SIZE, treePut, hashPut);

        // get 비교
        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) treeMap.get(i);
        long treeGet = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) hashMap.get(i);
        long hashGet = System.nanoTime() - start;

        System.out.printf("get() %,d회 → TreeMap: %,d ns  HashMap: %,d ns%n", SIZE, treeGet, hashGet);
        System.out.println("→ TreeMap은 정렬 유지·범위 연산 필요 시 선택, 단순 조회는 HashMap 우위");
        System.out.println();
    }
}
