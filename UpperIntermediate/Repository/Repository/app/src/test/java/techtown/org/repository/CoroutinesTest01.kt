package techtown.org.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

/*
코루틴을 테스트 코드로 활용해서 써보기 위한 클래스
 */
class CoroutinesTest01 {

    @Test
    fun test01() = runBlocking {
        val time = measureTimeMillis { // 시간이 얼마나 걸리는지 체크
            val name = getFirstName()
            val lastname = getLastName()
            print("Hello $name $lastname")
        }
        print("measure time : $time")
    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis { // async로 체크, 비동기로 동시에 여러개 처리해서 더 빠름
            val name = async {getFirstName()}
            val lastname = async {getLastName()}
            print("Hello ${name.await()} ${lastname.await()}")
        }
        print("measure time : $time")
    }

    suspend fun getFirstName(): String {
        delay(1000)
        return "이"
    }

    suspend fun getLastName(): String {
        delay(1000)
        return "기정"
    }
}