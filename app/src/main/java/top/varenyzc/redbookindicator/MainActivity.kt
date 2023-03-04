package top.varenyzc.redbookindicator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.youth.banner.Banner
import com.youth.banner.listener.OnPageChangeListener

class MainActivity : AppCompatActivity() {

    companion object {

        val imageUrls = listOf(
            "https://img.zcool.cn/community/01b72057a7e0790000018c1bf4fce0.png",
            "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
            "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",
            "https://img.zcool.cn/community/01700557a7f42f0000018c1bd6eb23.jpg",
            "https://img.zcool.cn/community/01b72057a7e0790000018c1bf4fce0.png",
            "https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg",
            "https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg",
            "https://img.zcool.cn/community/01700557a7f42f0000018c1bd6eb23.jpg"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val indicatorView = findViewById<DotIndicatorView>(R.id.indicatorView)
        indicatorView.setCount(imageUrls.size)
        val banner = findViewById<Banner<String, ImageAdapter>>(R.id.banner)
        banner.apply {
            addBannerLifecycleObserver(this@MainActivity)
            setBannerRound(20f)
            setAdapter(ImageAdapter(imageUrls))
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

                }

                override fun onPageSelected(p0: Int) {
                    indicatorView.setSelectedIndex(p0)
                }

                override fun onPageScrollStateChanged(p0: Int) {
                }
            })
        }
    }
}