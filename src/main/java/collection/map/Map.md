# Map 인터페이스

## 개요
- **키(key) - 값(value) 쌍**으로 데이터를 저장하는 자료구조
- 키는 중복 불가, 값은 중복 허용
- 키 하나에 값 하나가 매핑됨

## 주요 구현체 비교

| 구현체 | 순서 | 성능 | 특징 |
|--------|------|------|------|
| `HashMap` | 미보장 | O(1) 평균 | 범용, 가장 빠름 |
| `LinkedHashMap` | 삽입 순서 유지 | O(1) 평균 | 순서가 필요할 때 |
| `TreeMap` | 키 정렬 순서 | O(log n) | 범위 검색, 정렬 필요 시 |
| `ConcurrentHashMap` | 미보장 | O(1) 평균 | 멀티스레드 환경 |

---

## Key 사용 원칙

### 1. Key는 불변(Immutable) 객체를 써야 한다

Map은 key를 `hashCode()`로 버킷 위치를 계산하고, `equals()`로 정확한 키를 비교한다.

key가 삽입 후 **변경되면** hashCode가 달라져서 해당 entry를 **영영 찾지 못하게 된다**.

```java
// 위험한 예시 - 가변 객체를 key로 사용
List<String> mutableKey = new ArrayList<>(List.of("a"));
map.put(mutableKey, "value");

mutableKey.add("b"); // key 내부 변경
map.get(mutableKey); // null 반환 - 찾을 수 없음
```

### 2. equals()와 hashCode()를 함께 올바르게 구현해야 한다

**계약(Contract)**
> 두 객체가 `equals()`로 같으면 반드시 `hashCode()`도 같아야 한다.

- `hashCode()`만 없으면: 동등한 객체가 다른 버킷에 들어가 찾을 수 없다
- `equals()`만 없으면: 같은 버킷 안에서 다른 객체로 판단해 중복 저장된다

```java
// hashCode를 override하지 않은 커스텀 클래스
map.put(new Person("홍길동"), "value");
map.get(new Person("홍길동")); // null - 다른 버킷에서 찾음
```

### 3. 좋은 Key vs 나쁜 Key

| 분류 | 예시 | 이유 |
|------|------|------|
| **좋은 key** | `String`, `Integer`, `Long` 등 래퍼 타입 | 불변 + equals/hashCode 완벽 구현 |
| **좋은 key** | `enum` | 불변 + 동일성 보장 |
| **좋은 key** | `record` (Java 16+) | 불변 + equals/hashCode 자동 생성 |
| **나쁜 key** | `ArrayList`, `HashMap` 등 가변 컬렉션 | 내부 상태 변경 시 key 손실 위험 |
| **나쁜 key** | equals/hashCode 미구현 커스텀 클래스 | 동등 비교 불가 |

### 핵심 요약
- **불변 객체**를 key로 사용한다
- 커스텀 클래스를 key로 쓸 때는 `equals()`와 `hashCode()`를 **반드시 함께** override한다
- `hashCode()`가 잘 분산될수록 충돌이 줄어 성능이 좋아진다
