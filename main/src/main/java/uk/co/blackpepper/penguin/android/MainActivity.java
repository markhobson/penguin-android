package uk.co.blackpepper.penguin.android;

import uk.co.blackpepper.penguin.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{
	// fields -----------------------------------------------------------------
	
	private PagerAdapter pagerAdapter;

	private ViewPager viewPager;

	private boolean created;
	
	// FragmentActivity methods -----------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// The main activity is not created until a server is configured.
		// Creation which would normally be done here is performed in onResume if
		// a server has been configued.
		created = false;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (!isServerUrlConfigured())
		{
			displaySettings();
		}
		else if (!created)
		{
			create();
		}
	}
	
	// Activity methods -------------------------------------------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.queue_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_settings:
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingsIntent);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// TabListener methods ----------------------------------------------------

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	// private methods --------------------------------------------------------

	/**
	 * create activity.
	 * 
	 * This would normally be done in the onCreate method. However, as we can not load data from the server until it has
	 * been configured, it is extracted here and called from onResume when details of a server are available.
	 */
	private void create()
	{
		pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

		// configure action bar
		
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// configure view pager
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		// add tabs

		for (int i = 0; i < pagerAdapter.getCount(); i++)
		{
			Tab tab = actionBar.newTab()
				.setText(pagerAdapter.getPageTitle(i))
				.setTabListener(this);
			
			actionBar.addTab(tab);
		}

		created = true;
	}

	private boolean isServerUrlConfigured()
	{
		String defaultServerUrl = getResources().getString(R.string.pref_default_server_url);
		String serverUrl = PreferenceUtils.getServerUrl(this);

		if (null == serverUrl || defaultServerUrl.equals(serverUrl))
		{
			return false;
		}
		
		return true;
	}

	private void displaySettings()
	{
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingsIntent);
	}
}
