package xyz.erichg

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*

class MainActivity : AppCompatActivity() {



    private var debugging = true
    private lateinit var canvasFragment: CanvasFragment




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        canvasFragment = CanvasFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment_container,canvasFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        if (debugging)
        {
            Log.d("Debugging","In onCreate")
        }

    }


    override fun onContextMenuClosed(menu: Menu) {
        super.onContextMenuClosed(menu)
        canvasFragment.onContextMenuClosed(menu)
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