import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.PodiumFragment
import com.example.myapplication.ProfileFragment
import com.example.myapplication.TrainFragment

class ViewPagerAdapter(fm: FragmentManager, private val tabCount: Int) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return tabCount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment() // Replace with your first fragment class
            1 -> PodiumFragment() // Replace with your second fragment class
            2 -> TrainFragment() // Replace with your third fragment class
            else -> throw IllegalArgumentException("Invalid tab position: $position")
        }
    }
}