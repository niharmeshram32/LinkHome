package au.edu.anu.cecs.linkhome.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import au.edu.anu.cecs.linkhome.R;
import au.edu.anu.cecs.linkhome.StateDesignPattern.User;

/** LoginActivity sets up the login tab fragment and sign up fragment
 * for the users to login and sign up accordingly
 * The data is stored on firebase for all the logged in users or those users who sign up
 *
 */
public class LoginActivity extends AppCompatActivity {

    // Firebase Authentication
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Instance of the User
        User user = User.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Tabs for Login and SignUp
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        mAuth = FirebaseAuth.getInstance();

        tabLayout.setupWithViewPager(viewPager);
        LoginAdapter loginAdapter = new LoginAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        Bundle bundle = new Bundle();
        bundle.putSerializable("USER", user);

        Fragment loginFragment = new LoginTabFragment();
        loginFragment.setArguments(bundle);

        loginAdapter.addFragment(loginFragment, "Login");
        loginAdapter.addFragment(new SignUpTabFragment(), "SignUp");
        viewPager.setAdapter(loginAdapter);
    }
}