package xyz.erichg
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

class CanvasFragment (): Fragment()
{

    private val debugging: Boolean = true
    private var teal : Int = -1
    private var blue : Int= -1
    private var navy: Int = -1
    private var mint : Int = -1

    private lateinit var ctx : Context

    private lateinit var canvasView: CanvasView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val parentView = inflater.inflate(R.layout.fragment_canvas,container,false)
        ctx = requireContext()
        teal = ContextCompat.getColor(ctx,R.color.teal_200)
        blue = ContextCompat.getColor(ctx,R.color.blue)
        navy = ContextCompat.getColor(ctx,R.color.navy)
        mint = ContextCompat.getColor(ctx,R.color.mint)
        canvasView = CanvasView(ctx)
        (parentView as ViewGroup).addView(canvasView)

        canvasView.contentDescription = getString(R.string.canvasDescription)

        registerForContextMenu(canvasView)
        immersiveMode()
        if( savedInstanceState != null)
        {
            val sharedPref = context?.getSharedPreferences("prefs",Context.MODE_PRIVATE)
            val allEntries: Map<String, *> = sharedPref?.all as Map<String, *>
            for((key,value) in allEntries)
            {
                Log.d("sharedPreference","$key : $value")
            }

            Log.d("sharedPreference","")

        }


        if(debugging)
        {
            Log.d("onCreateView ","ctx $ctx")
        }

        return parentView

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val sharedPref = context?.getSharedPreferences("prefs",Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.putInt("shotsTaken",canvasView.shotsTaken)
        editor?.putInt("distance",canvasView.distance)




    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.context_menu,menu)

        canvasView.isContextMenuOpen = true
        if(debugging)
        {
            Log.d("onCreateContextMenu","$this")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        return when (item.itemId)
        {
            R.id.debug->
            {
                canvasView.setDebug()
                Log.d("inside debug branch","")
                true
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
    fun onContextMenuClosed(menu: Menu)
    {
        canvasView.isContextMenuOpen = false
        canvasView.isContextMenuClosed = true
        immersiveMode()
    }


    private fun immersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = WindowCompat.getInsetsController(
                requireActivity().window,
                requireActivity().window.decorView
            )

            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            windowInsetsController.show(WindowInsetsCompat.Type.captionBar())
        } else {
            // your code for older Android versions
            Log.d("deprecation","")


            @Suppress("DEPRECATION")

            requireActivity()?.window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }





}