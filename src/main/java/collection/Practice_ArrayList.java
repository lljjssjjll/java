package collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Practice_ArrayList {
    private static final int SIZE = 10_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 인덱스 기반 랜덤 접근이 빈번하고 순서가 중요한 데이터 저장
    static void demonstratePurpose() {
        System.out.println("=== [용도] ArrayList ===");
        ArrayList<String> list = new ArrayList<>();
        list.add("A"); list.add("B"); list.add("C"); list.add("D");
        System.out.println("초기 리스트       : " + list);

        list.add(1, "X");
        System.out.println("index 1에 X 삽입  : " + list);

        list.remove(2);
        System.out.println("index 2 삭제       : " + list);

        String val = list.get(1);
        System.out.println("index 1 조회       : " + val);

        int idx = list.indexOf("C");
        System.out.println("C의 인덱스         : " + idx);

        List<String> sub = list.subList(0, 2);
        System.out.println("subList(0,2)       : " + sub);
        System.out.println();
    }

    // 장점: O(1) 랜덤 접근 / 연속 메모리 구조로 CPU 캐시 효율 우수
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] ArrayList ===");
        ArrayList<Integer> list = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) list.add(i);

        // 임의 인덱스 직접 접근 — 내부 배열 포인터 연산 1회
        long start = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < SIZE; i++) sum += list.get(i);
        long elapsed = System.nanoTime() - start;

        System.out.println("O(1) 랜덤 접근 (" + SIZE + "회) : " + elapsed / 1_000_000 + " ms  (합계=" + sum + ")");
        System.out.println("연속 메모리 → CPU 캐시 라인 히트율 높음");
        System.out.println();
    }

    // 단점: 중간 삽입/삭제 O(n) 쉬프트 / 용량 초과 시 새 배열 복사(×1.5)
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] ArrayList ===");
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) list.add(i);

        // 맨 앞 삽입마다 기존 요소 전체 오른쪽 쉬프트 → O(n)
        long start = System.nanoTime();
        for (int i = 0; i < 1_000; i++) list.add(0, -i);
        long shiftTime = System.nanoTime() - start;
        System.out.println("맨 앞 삽입 1,000회 (O(n) 쉬프트) : " + shiftTime / 1_000_000 + " ms");

        // 초기 용량 없이 삽입 반복 → 내부 배열 재할당 반복 발생
        ArrayList<Integer> growing = new ArrayList<>(1);
        start = System.nanoTime();
        for (int i = 0; i < 100_000; i++) growing.add(i);
        long growTime = System.nanoTime() - start;
        System.out.println("용량 1→100,000 확장 (배열 복사 반복) : " + growTime / 1_000_000 + " ms");
        System.out.println();
    }

    // 성능: vs LinkedList — 랜덤 접근(ArrayList 우위) / 맨 앞 삽입(LinkedList 우위)
    static void demonstratePerformance() {
        System.out.println("=== [성능] ArrayList vs LinkedList ===");

        ArrayList<Integer> al = new ArrayList<>(SIZE);
        LinkedList<Integer> ll = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) { al.add(i); ll.add(i); }

        // 랜덤 접근: ArrayList O(1) vs LinkedList O(n)
        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) al.get(i);
        long alGet = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) ll.get(i);
        long llGet = System.nanoTime() - start;

        System.out.printf("랜덤 접근 %,d회  → ArrayList: %,d ns  LinkedList: %,d ns%n", SIZE, alGet, llGet);

        // 맨 앞 삽입: ArrayList O(n) 쉬프트 vs LinkedList O(1) 노드 교체
        ArrayList<Integer> al2 = new ArrayList<>();
        LinkedList<Integer> ll2 = new LinkedList<>();

        start = System.nanoTime();
        for (int i = 0; i < 5_000; i++) al2.add(0, i);
        long alInsert = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 5_000; i++) ll2.addFirst(i);
        long llInsert = System.nanoTime() - start;

        System.out.printf("맨 앞 삽입 5,000회 → ArrayList: %,d ns  LinkedList: %,d ns%n", alInsert, llInsert);
        System.out.println();
    }
}