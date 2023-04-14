package xyz.erichg

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.view.ViewCompat.getWindowInsetsController
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {



    private var debugging = true

    private var teal : Int = -1
    private var blue : Int= -1
    private var navy: Int = -1
    private var mint : Int = -1

    private lateinit var canvasView: CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        teal = ContextCompat.getColor(this,R.color.teal_200)
        blue = ContextCompat.getColor(this,R.color.blue)
        navy = ContextCompat.getColor(this,R.color.navy)
        mint = ContextCompat.getColor(this,R.color.mint)

        canvasView = CanvasView(this)
        canvasView.contentDescription = getString(R.string.canvasDescription)
        immersiveMode()



        setContentView(canvasView)

        registerForContextMenu(canvasView)






        if (debugging)
        {
            Log.d("Debugging","In onCreate")
        }

    }



    private fun immersiveMode()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = WindowCompat.getInsetsController(window,window.decorView)

            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController?.hide(WindowInsetsCompat.Type.navigationBars())

            windowInsetsController?.show(WindowInsetsCompat.Type.captionBar())
            Log.d("support :" ," $supportActionBar")





        } else {
            // your code for older Android versions
            Log.d("deprecation","")

            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            @Suppress("DEPRECATION")
            canvasView.systemUiVisibility =  (
                        View.SYSTEM_UI_FLAG_FULLSCREEN  or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }




    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu,menu)
        if(debugging)
        {
            Log.d("onCreateContextMenu","")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)
        {
            R.id.title_menu->
            {
                return true
            }
            R.id.teal ->
            {
                canvasView.setBackgroundColor(teal)
                true
            }
            R.id.blue ->
            {
                canvasView.setBackgroundColor(blue)
                true
            }
            R.id.mint->
            {

                canvasView.setBackgroundColor(mint)
                true

            }
            R.id.navy->
            {

                canvasView.setBackgroundColor(navy)
                true

            }
            else -> super.onContextItemSelected(item)
        }
    }








    /**
    private fun boom() {
        gameView.setImageBitmap(blankBitmap)
        canvas.drawColor ( Color.argb(254,255,0,0))
        paint.color = Color.argb(254,255,255,255)

        newGame()
    }

    **/


}