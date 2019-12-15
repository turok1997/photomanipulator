package ee.ut.photomanipulation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.soundcloud.android.crop.Crop
import ee.ut.photomanipulation.history.EventHistory
import ee.ut.photomanipulation.operations.MirrorOperation
import kotlinx.android.synthetic.main.activity_photo_edit.*
import java.io.File
import java.lang.ref.WeakReference


class PhotoEditActivity : AppCompatActivity() {

    private lateinit var history:EventHistory
    private lateinit var undo:MenuItem
    private lateinit var redo:MenuItem
    private lateinit var loadingFeedback: LoadingFeedback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_edit)
        loadingFeedback = LoadingFeedback(progressbar, this)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val imagePath = intent.getStringExtra("imagePath")
        val imageUri = Uri.fromFile(File(imagePath))
        history = EventHistory(imageUri)

        val outUri = Uri.parse("file:///storage/emulated/0/output.jpeg")
        loadingFeedback.drawPicture(imageUri)

        btn_crop.setOnClickListener{ view -> Crop.of(imageUri, outUri).start(this)}
        btn_mirror_v.setOnClickListener{ view -> MirrorOperation(loadingFeedback, history, this, false).execute() }
        btn_mirror_h.setOnClickListener{ view -> MirrorOperation(loadingFeedback, history, this, true).execute() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            //show cropped pic
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_activity_menu, menu)
        undo = menu?.findItem(R.id.undo)!!
        redo = menu.findItem(R.id.redo)!!
        loadingFeedback.menu = WeakReference(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.undo -> {
                loadingFeedback.drawPicture(history.undo())
                redo.isEnabled = true
                if (!history.canUndo()) { item.isEnabled = false }
                return true
            }
            R.id.redo -> {
                loadingFeedback.drawPicture(history.redo())
                undo.isEnabled = true
                if (!history.canRedo()) { item.isEnabled = false }
                return true
            }
            R.id.save -> {

            }
        }

        return false
    }
}
