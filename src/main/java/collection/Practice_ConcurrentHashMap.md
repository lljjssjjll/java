# ConcurrentHashMap

## 용도
- **멀티스레드 환경**에서 안전한 키-값 공유 저장소
- 높은 동시 읽기·쓰기가 필요한 캐시, 카운터, 공유 상태 관리
- `HashMap` + 외부 동기화보다 **높은 처리량**이 필요할 때
- 내부: **버킷 단위 CAS + synchronized** (Java 8+)  
  (Java 7 이전: 세그먼트 락 방식)

## 장점
| 항목 | 내용 |
|------|------|
| **스레드 안전** | 별도 동기화 없이 다중 스레드에서 안전하게 사용 |
| **높은 동시성** | 서로 다른 버킷은 동시 접근 허용 → `synchronizedMap`의 전체 락보다 빠름 |
| **원자적 복합 연산** | `putIfAbsent`, `compute`, `merge` 등 원자적 메서드 제공 |

## 단점
| 항목 | 내용 |
|------|------|
| **단일 스레드 오버헤드** | CAS·volatile 접근 비용으로 `HashMap` 대비 느림 |
| **size() 추정값** | `LongAdder` 합산 방식으로 동시 수정 중엔 정확하지 않을 수 있음 |
| **null 키·값 불가** | `put(null, v)` → `NullPointerException` (HashMap은 허용) |
| **복합 연산 원자성** | `get` → `put` 사이에 다른 스레드 끼어들 수 있음 → `compute` 사용 |

## 성능

| 연산 | ConcurrentHashMap | synchronizedMap | 비고 |
|------|------------------|----------------|------|
| 단일 스레드 put | 느림 | 느림 | 둘 다 HashMap보다 오버헤드 있음 |
| 다중 스레드 put | **빠름** | 느림 | 버킷 단위 병렬 vs 전체 락 직렬 |
| 다중 스레드 get | **빠름** | 느림 | 읽기는 락 없이 volatile 접근 |
| `null` 키/값 | X | △ (HashMap 기반이면 가능) | CHM은 불허 |
| 원자적 복합 연산 | **지원** (`compute` 등) | 미지원 (수동 동기화 필요) | |

### HashMap vs ConcurrentHashMap vs synchronizedMap 요약

| 항목 | HashMap | ConcurrentHashMap | synchronizedMap |
|------|---------|------------------|----------------|
| 스레드 안전 | X | O | O |
| 단일 스레드 성능 | 가장 빠름 | 중간 | 중간 |
| 다중 스레드 성능 | 사용 불가 | **가장 빠름** | 가장 느림 |
| null 키 | O | X | △ |

> **결론**: 단일 스레드 → HashMap / 멀티스레드 고성능 → ConcurrentHashMap / 레거시·단순 동기화 → synchronizedMap
