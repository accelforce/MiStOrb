package net.accelf.mistorb.drawer;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import net.accelf.mistorb.R;
import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.db.SaveDataUtil;
import net.accelf.mistorb.network.RetrofitHelper;

import okhttp3.HttpUrl;

public class DrawerHelper {

    private Activity activity;
    private InstancePickUtil instancePicker;
    private SaveDataUtil saveData;

    public DrawerHelper(Activity activity) {
        this.activity = activity;
        instancePicker = new InstancePickUtil(activity);
        saveData = new SaveDataUtil(activity);
    }

    public Drawer buildDrawer(AccountHeader.OnAccountHeaderListener accountHeaderListener,
                              Drawer.OnDrawerItemClickListener drawerItemClickListener, Toolbar toolbar) {
        DrawerBuilder builder = new DrawerBuilder().withActivity(activity);
        builder = addAccountHeader(builder, accountHeaderListener);
        builder = addDrawerItems(builder, drawerItemClickListener);
        builder = configureWithToolBar(builder, toolbar);
        return builder.build();
    }

    private DrawerBuilder addAccountHeader(DrawerBuilder builder, AccountHeader.OnAccountHeaderListener listener) {
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(activity)
                .withOnAccountHeaderListener(listener);
        accountHeaderBuilder = addProfileItems(accountHeaderBuilder);
        AccountHeader header = accountHeaderBuilder.build();
        for (IProfile profile:header.getProfiles()){
            if(profile.getName().toString().equals(instancePicker.selectedInstance())){
                header.setActiveProfile(profile);
            }
        }
        return builder.withAccountHeader(header);
    }

    @NonNull
    private AccountHeaderBuilder addProfileItems(AccountHeaderBuilder builder) {
        String[] domains = new String[0];
        domains = saveData.listCookies().toArray(domains);
        if (domains == null) {
            return builder;
        }
        for (String domain : domains) {
            builder.addProfiles(createProfileDrawerItem(domain));
        }
        ProfileSettingDrawerItem addNewDrawerItem = new ProfileSettingDrawerItem()
                .withIdentifier(DrawerItem.ITEM_ADD_NEW.getId())
                .withIcon(R.drawable.ic_add)
                .withIconColorRes(R.color.colorText)
                .withIconTinted(true)
                .withName(activity.getString(R.string.drawer_item_add));
        return builder.addProfiles(addNewDrawerItem);
    }

    private static ProfileDrawerItem createProfileDrawerItem(String domain) {
        return new ProfileDrawerItem()
                .withName(domain)
                .withIcon(buildFaviconUrl(domain));
    }

    private static String buildFaviconUrl(String domain) {
        HttpUrl url = RetrofitHelper.generateEndpoint(domain)
                .newBuilder()
                .addPathSegments("/favicon.ico")
                .build();
        return url.toString();
    }

    private DrawerBuilder addDrawerItems(DrawerBuilder builder, Drawer.OnDrawerItemClickListener listener) {
        for (DrawerItem id : DrawerItem.values()) {
            IDrawerItem item = new DividerDrawerItem();
            switch (id) {
                case ITEM_RE_LOGIN:
                    item = new PrimaryDrawerItem()
                            .withIdentifier(id.getId())
                            .withIcon(R.drawable.ic_refresh)
                            .withIconColorRes(R.color.colorText)
                            .withIconTintingEnabled(true)
                            .withName(activity.getString(R.string.drawer_item_re_login))
                            .withDescription(activity.getString(R.string.drawer_item_desc_re_login));
            }
            builder.addDrawerItems(item);
        }
        return builder
                .withOnDrawerItemClickListener(listener)
                .withSelectedItem(-1)
                .withHasStableIds(true);
    }

    private DrawerBuilder configureWithToolBar(DrawerBuilder builder, Toolbar toolbar) {
        return builder.withToolbar(toolbar);
    }

    public void initializeImageLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return activity.getDrawable(R.drawable.ic_cloud);
            }
        });
    }

    public enum DrawerItem {
        ITEM_RE_LOGIN(0),
        ITEM_ADD_NEW(1);

        private final int id;

        DrawerItem(final int id) {
            this.id = id;
        }

        public static DrawerItem fromId(int id) {
            for (DrawerItem item : DrawerItem.values()) {
                if (item.getId() == id) {
                    return item;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}
