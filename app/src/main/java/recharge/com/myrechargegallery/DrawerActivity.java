package recharge.com.myrechargegallery;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
//import android.support.design.widget.BottomNavigationView;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
//import android.view.View;
//import android.support.design.widget.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PrefManager prefManager;
    boolean doubleBackToExitPressedOnce = false;
    public static String title = "Home";

    static Toolbar toolbar;
    static MenuItem itemBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        toolbar.setBackground(new ColorDrawable(Color.parseColor("#333333")));
        toolbar.setTitle("Home");

        prefManager = new PrefManager(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);   //show original color of icons

        //prefManager.setUserType("admin");
        Menu menu = navigationView.getMenu();
        String userType = prefManager.getUserType();
        if(userType.trim().equalsIgnoreCase("admin")) {
            menu.removeGroup(R.id.retailer);
            menu.removeGroup(R.id.distributor);
            menu.removeGroup(R.id.home);
        } else if(userType.trim().equalsIgnoreCase("distributor")) {
            menu.removeGroup(R.id.retailer);
            menu.removeGroup(R.id.admin);
            menu.removeGroup(R.id.home);
        } else if(userType.trim().equalsIgnoreCase("retailer")) {
            menu.removeGroup(R.id.admin);
            menu.removeGroup(R.id.distributor);
            menu.removeGroup(R.id.home);
        } else if(userType.trim().equalsIgnoreCase("home")) {
            menu.removeGroup(R.id.retailer);
            menu.removeGroup(R.id.distributor);
            menu.removeGroup(R.id.admin);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setItemIconTintList(null);   //show original color of icons

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.navigation_recharge:
                        selectedFragment = PrepaidRecharge.newInstance();
                        break;
                    case R.id.navigation_dth:
                        selectedFragment = DTHRecharge.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, HomeFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        String title = toolbar.getTitle().toString().trim();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment selectedFragment = null;
            switch (title) {
                case "Home" :
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        return;
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 2000);
                    //selectedFragment = new HomeFragment();
                    break;
                case "Create Distributor" :
                    selectedFragment = new DistributorsList();
                    break;
                case "Create Retailer" :
                    selectedFragment = new RetailersList();
                    break;
                case "Add Beneficiary" :
                    selectedFragment = new MoneyTransfer();
                    break;
                default :
                    selectedFragment = new HomeFragment();
                    break;
            }
            if(selectedFragment!=null) {
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.container, selectedFragment);
                transaction1.commit();
            }
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        itemBalance = menu.findItem(R.id.action_balance);
        //itemBalance.setTitle("Rs. " + prefManager.getBalance());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Drawable drawable = item.getIcon();
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            //drawable.setAlpha(alpha);
        }


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_pwd) {
            //return true;
            Fragment selectedFragment = null;
            selectedFragment = ChangePassword.newInstance();
            if(selectedFragment!=null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, selectedFragment);
                transaction.commit();
            }
            //Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_change_pin) {
            prefManager.setUserPin("");
            Intent intent = new Intent(getApplicationContext(), PinActivity.class);
            startActivity(intent);
            finish();
            //Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_contact_us) {
            //Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
            Fragment selectedFragment = null;
            selectedFragment = AboutUs.newInstance();
            if(selectedFragment!=null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, selectedFragment);
                transaction.commit();
            }
        } else if (id == R.id.action_notification) {
            //Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
            Fragment selectedFragment = null;
            selectedFragment = NotificationFragment.newInstance();
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, selectedFragment);
                transaction.commit();
            }
        } else if (id == R.id.action_balance) {
            //Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_admin_prepaid_recharge) {
            selectedFragment = PrepaidRecharge.newInstance();
        } else if (id == R.id.nav_admin_postpaid_recharge) {
            selectedFragment = PostpaidRecharge.newInstance();
        } else if (id == R.id.nav_admin_dth_recharge) {
            selectedFragment = DTHRecharge.newInstance();
        } else if (id == R.id.nav_admin_electricity_bill) {
            selectedFragment = ElectricityBillPayment.newInstance();
        } else if (id == R.id.nav_admin_gas_bill) {
            selectedFragment = GasBillPayment.newInstance();
        } else if (id == R.id.nav_admin_fund_transfer) {
            selectedFragment = MoneyTransfer.newInstance();
        } else if (id == R.id.nav_share) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (id == R.id.nav_logout) {
            prefManager.clearPreference();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_dist_change_pin) {
//            prefManager.setUserPin("");
//            Intent intent = new Intent(getApplicationContext(), PinActivity.class);
//            startActivity(intent);
//            finish();
            Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_dist_change_pwd) {
            selectedFragment = ChangePassword.newInstance();
        } else if (id == R.id.nav_dist_contact_us) {
            selectedFragment = AboutUs.newInstance();
        } else if (id == R.id.nav_ret_change_pin) {
//            prefManager.setUserPin("");
//            Intent intent = new Intent(getApplicationContext(), PinActivity.class);
//            startActivity(intent);
//            finish();
            Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_ret_change_pwd) {
            selectedFragment = ChangePassword.newInstance();
        } else if (id == R.id.anav_ret_contact_us) {
            selectedFragment = AboutUs.newInstance();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(selectedFragment!=null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, selectedFragment);
            transaction.commit();
        }

        return true;
    }

    public static void setTitle(String title) {
        toolbar.setTitle(title);
//        if(title.equalsIgnoreCase("home"))
//            toolbar.setBackground(new ColorDrawable(Color.parseColor("#333333")));
//        else
//            toolbar.setBackground(new ColorDrawable(Color.parseColor("#42a342")));
    }
}
