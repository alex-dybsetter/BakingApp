package net.alexblass.bakingapp.utilities;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new BakingViewsFactory(this.getApplicationContext(),
                intent));
    }
}