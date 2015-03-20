package cl.gob.modernizacion.itransantiago.misc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import cl.gob.modernizacion.itransantiago.R;

public class iTransantiagoRenderer<T extends MyItem> extends DefaultClusterRenderer<T> {

    private BitmapDescriptor icon;
    private int color;

    private IconGenerator iconGenerator;
    private BitmapDescriptor descriptor;

    public iTransantiagoRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager, BitmapDescriptor icon, int color) {
        super(context, map, clusterManager);

        this.icon = icon;

        ShapeDrawable mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        LayerDrawable background = new LayerDrawable(new Drawable[]{ mColoredCircleBackground});
        mColoredCircleBackground.getPaint().setColor(color);
        mColoredCircleBackground.getPaint().setAlpha(191);

        iconGenerator = new IconGenerator(context);
        iconGenerator.setBackground(background);
        iconGenerator.setContentView(makeSquareTextView(context));
        iconGenerator.setTextAppearance(R.style.ClusterIcon_TextAppearance);
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(R.id.text);
        int twelveDpi = (int) (12 * context.getResources().getDisplayMetrics().density);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        squareTextView.setTextColor(Color.WHITE);
        return squareTextView;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem clusterItem, MarkerOptions markerOptions) {
        markerOptions.title(clusterItem.getTitle());
        markerOptions.snippet(clusterItem.getSnippet());
        markerOptions.icon(icon);

    }

    protected void onBeforeClusterRendered(com.google.maps.android.clustering.Cluster<T> cluster, com.google.android.gms.maps.model.MarkerOptions markerOptions)
    {
        super.onBeforeClusterRendered(cluster, markerOptions);


        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon("" + cluster.getItems().size())));
    }

    protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
        return cluster.getSize() > 1;
    }


}
