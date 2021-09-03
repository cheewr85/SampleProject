package techtown.org.recorder

// 녹음기의 상태를 나타내기 위해 만든 열거형 클래스
// 상태를 미리 지정해줌, 녹음 상태마다 보여지는 버튼이 달라지기 위해서 상태값에 따라서 UI가 달라지기 때문에 미리 정의함
enum class State {
    BEFORE_RECORDING,
    ON_RECORDING,
    AFTER_RECORDING,
    ON_PLAYING
}