package xyz.erichg

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class GameOverFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentView = inflater.inflate(R.layout.gameover_fragment,container,false)
        alertGameOver()
        return parentView
    }
    private fun alertGameOver()
    {

         val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setMessage("Game Over play again?")
        builder.setPositiveButton("OK") { _, _ ->
            val fragmentManager = requireActivity().supportFragmentManager

            fragmentManager.popBackStack()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            // handle Cancel button click
            requireActivity().finish()

        }
        builder.create()


        builder.show()




    }
}