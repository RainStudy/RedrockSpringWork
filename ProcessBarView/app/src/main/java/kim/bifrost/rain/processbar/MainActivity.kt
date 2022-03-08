package kim.bifrost.rain.processbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pbv1 = findViewById<ProcessBarView>(R.id.pbv1)
        val pbv2 = findViewById<ProcessBarView>(R.id.pbv2)
        val pbv3 = findViewById<ProcessBarView>(R.id.pbv3)
        val pool = Executors.newFixedThreadPool(3)
        pool.apply {
            submit(getTask(pbv1))
            submit(getTask(pbv2))
            submit(getTask(pbv3))
        }
    }

    private fun getTask(bar: ProcessBarView): Runnable {
        return Runnable {
            // 设总下载量为1000
            val size = 1000
            repeat(size) {
                bar.post {
                    bar.percent += 0.001f
                }
                Thread.sleep(10)
            }
        }
    }
}