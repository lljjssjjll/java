package collection;

import java.util.ArrayList;
import java.util.LinkedList;

public class Practice_LinkedList {
    private static final int SIZE = 10_000;

    public static void main(String[] args) {
        demonstratePurpose();
        demonstrateAdvantages();
        demonstrateDisadvantages();
        demonstratePerformance();
    }

    // 용도: 양 끝 삽입/삭제가 빈번하거나 Deque(스택·큐)로 활용
    static void demonstratePurpose() {
        System.out.println("=== [용도] LinkedList ===");

        // Deque 사용
        LinkedList<String> deque = new LinkedList<>();
        deque.addLast("B");
        deque.addFirst("A");
        deque.addLast("C");
        System.out.println("deque 구성          : " + deque);

        String head = deque.removeFirst();
        String tail = deque.removeLast();
        System.out.println("removeFirst()       : " + head + "  →  남은: " + deque);
        System.out.println("removeLast()        : " + tail + "  →  남은: " + deque);
        System.out.println("peekFirst()         : " + deque.peekFirst() + "  (제거 없음)");

        // 큐 사용 (FIFO)
        LinkedList<Integer> queue = new LinkedList<>();
        queue.offer(1); queue.offer(2); queue.offer(3);
        System.out.println("queue offer 1,2,3   : " + queue);
        System.out.println("queue poll()        : " + queue.poll() + "  →  남은: " + queue);

        // 스택 사용 (LIFO)
        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(10); stack.push(20); stack.push(30);
        System.out.println("stack push 10,20,30 : " + stack);
        System.out.println("stack pop()         : " + stack.pop() + "  →  남은: " + stack);
        System.out.println();
    }

    // 장점: 양 끝 삽입/삭제 O(1) — 헤드·테일 포인터만 교체, 데이터 이동 없음
    static void demonstrateAdvantages() {
        System.out.println("=== [장점] LinkedList ===");
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) list.add(i);

        long start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) {
            list.addFirst(-i);   // 헤드 포인터 교체 → O(1)
            list.removeFirst();  // 헤드 포인터 교체 → O(1)
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("addFirst+removeFirst " + SIZE + "회 : " + elapsed / 1_000_000 + " ms  (O(1) 반복)");
        System.out.println();
    }

    // 단점: 인덱스 접근 O(n) — 노드를 하나씩 따라가야 함 / prev·next 포인터 메모리 오버헤드
    static void demonstrateDisadvantages() {
        System.out.println("=== [단점] LinkedList ===");
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) list.add(i);

        // get(index): 중간 인덱스까지 노드를 순차 탐색 → O(n)
        long start = System.nanoTime();
        for (int i = 0; i < 1_000; i++) list.get(SIZE / 2);
        long elapsed = System.nanoTime() - start;
        System.out.println("get(중간 인덱스) 1,000회 : " + elapsed / 1_000_000 + " ms  (O(n) 탐색)");

        // 노드당 오브젝트 헤더 + 데이터 + prev + next 포인터 → ArrayList 대비 메모리 약 3배
        System.out.println("노드 구조: [prev | data | next]  →  ArrayList 배열 원소 대비 메모리 오버헤드");
        System.out.println();
    }

    // 성능: vs ArrayList — 맨 앞 삽입(LinkedList 우위) / 랜덤 접근(ArrayList 우위)
    static void demonstratePerformance() {
        System.out.println("=== [성능] LinkedList vs ArrayList ===");

        // 맨 앞 삽입
        LinkedList<Integer> ll = new LinkedList<>();
        ArrayList<Integer> al = new ArrayList<>();

        long start = System.nanoTime();
        for (int i = 0; i < 5_000; i++) ll.addFirst(i);
        long llInsert = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 5_000; i++) al.add(0, i);
        long alInsert = System.nanoTime() - start;

        System.out.printf("맨 앞 삽입 5,000회  → LinkedList: %,d ns  ArrayList: %,d ns%n", llInsert, alInsert);

        // 랜덤 접근
        LinkedList<Integer> ll2 = new LinkedList<>();
        ArrayList<Integer> al2 = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) { ll2.add(i); al2.add(i); }

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) ll2.get(i);
        long llGet = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < SIZE; i++) al2.get(i);
        long alGet = System.nanoTime() - start;

        System.out.printf("랜덤 접근 %,d회    → LinkedList: %,d ns  ArrayList: %,d ns%n", SIZE, llGet, alGet);
        System.out.println();
    }
}
