package com.clinicalaluz.clinicaapp.Fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import com.clinicalaluz.clinicaapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class DialogoFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var  nombnredoctor="aaa"
    var  nombreespecialidad=""

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        open fun newInstance(): DialogoFragment? {
            return DialogoFragment()
        }
    }


    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.fragment_dialogo, null)

        var tvnamedoctor :TextView =contentView.findViewById(R.id.tvnomvredoctor4)
        var tvespecialidad:TextView=contentView.findViewById(R.id.tvespecialidad4)

        tvnamedoctor.text=nombnredoctor
        tvespecialidad.text=nombreespecialidad

        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

    }
}