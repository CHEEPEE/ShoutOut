package com.announcement.schol.infoboard.postmodule.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.announcement.schol.infoboard.R;
import com.announcement.schol.infoboard.postmodule.activities.CreateShoutout;

/**
 * Created by Keji's Lab on 31/10/2017.
 */

public class FreedomWallFragement extends Fragment {
    private View view;
    private FloatingActionButton shoutOut;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fregment_freedom_wall,container,false);
        shoutOut = (FloatingActionButton) view.findViewById(R.id.create_shoutout);
        shoutOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;
    }

}
