# ArrayDeque

## 용도
- **스택(LIFO)** 구현 — `Stack` 클래스보다 권장
- **큐(FIFO)** 구현 — `LinkedList` 기반 큐보다 빠름
- **덱(Deque)** — 양 끝에서 삽입·삭제가 모두 필요한 경우
- BFS 큐, DFS 스택, 슬라이딩 윈도우 등 알고리즘에서 자주 활용
- 내부: **원형 배열(circular array)** 구조

## 장점
| 항목 | 내용 |
|------|------|
| **양 끝 O(1)** | `addFirst/addLast/removeFirst/removeLast` = head·tail 인덱스 이동만 |
| **캐시 친화적** | 연속 메모리(배열)로 LinkedList 대비 캐시 효율 우수 |
| **GC 부담 없음** | LinkedList처럼 노드 객체를 매번 할당·해제하지 않음 |
| **Stack보다 빠름** | `Stack`은 `Vector` 기반(synchronized) → 불필요한 락 오버헤드 없음 |

## 단점
| 항목 | 내용 |
|------|------|
| **null 허용 안 함** | `offer(null)` → `NullPointerException` |
| **인덱스 접근 없음** | `get(i)` API 미제공, 중간 원소 접근 불가 |
| **동기화 없음** | 멀티스레드 환경에서 외부 동기화 필요 |
| **배열 재할당** | 용량 초과 시 2배 크기 배열로 복사 (드물게 발생) |

## 성능

| 연산 | ArrayDeque | LinkedList | 비고 |
|------|-----------|------------|------|
| `addFirst/addLast` | **O(1)** | O(1) | 인덱스 이동 vs 노드 생성·연결 |
| `removeFirst/removeLast` | **O(1)** | O(1) | 인덱스 이동 vs 노드 해제 |
| `peek()` | **O(1)** | O(1) | head 인덱스 배열 접근 vs head 포인터 |
| 실제 처리량 | **빠름** | 느림 | 캐시 효율·GC 부담 차이 |
| 인덱스 접근 | X | O(n) | 둘 다 지원 미흡 |

> **결론**: 스택·큐·덱 용도라면 LinkedList 대신 **ArrayDeque** 사용 권장 (성능·GC 모두 우위)
