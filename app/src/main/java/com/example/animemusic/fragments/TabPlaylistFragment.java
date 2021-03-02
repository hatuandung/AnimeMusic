package com.example.animemusic.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animemusic.R;
import com.example.animemusic.activity.MainActivity;
import com.example.animemusic.adapter.TabPlaylistAdapter;
import com.example.animemusic.dao.AppDatabase;
import com.example.animemusic.interfaces.CommunicationInterface;
import com.example.animemusic.models.Playlist;
import com.example.animemusic.view.ConfirmRemoveMyPlaylistDialog;
import com.example.animemusic.view.LocalPlaylistOptionsDialog;
import com.example.animemusic.view.SaveMyPlaylistDialog;

import java.util.ArrayList;

public class TabPlaylistFragment extends BaseFragment implements TabPlaylistAdapter.HeaderListener, TabPlaylistAdapter.ItemListener {

    private RecyclerView recyclerView;
    private TabPlaylistAdapter adapter;
    private ArrayList<Playlist> data = new ArrayList<>();

    private CommunicationInterface communicationInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            this.communicationInterface = (CommunicationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement onViewSelected!");
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_profile_tab_playlist;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        recyclerView = findViewByID(R.id.item_rv);
        adapter = new TabPlaylistAdapter(getLayoutInflater());
        recyclerView.setAdapter(adapter);
        adapter.setHeaderListener(this);
        adapter.setItemListener(this);
        getData();
    }

    public void getData() {
        data.clear();
        data.addAll(AppDatabase.getInstance(getActivity()).getPlaylistDao().getAll());
        if (adapter != null) {
            adapter.setData(data);
        }
    }

    @Override
    public void onHeaderClicked() {
        final SaveMyPlaylistDialog saveMyPlaylistDialog = new SaveMyPlaylistDialog();
        saveMyPlaylistDialog.show(getChildFragmentManager(), saveMyPlaylistDialog.getTag());
        saveMyPlaylistDialog.setOnActionListener(new SaveMyPlaylistDialog.OnActionListener() {
            public void onOk(String str) {
                Playlist playlist = new Playlist();
                playlist.setName(str);
                try {
                    AppDatabase.getInstance(getActivity()).getPlaylistDao().insert(playlist);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                getData();
                saveMyPlaylistDialog.dismiss();
            }

            public void onCancel() {
                saveMyPlaylistDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        int id = data.get(position).getId();
        String name = data.get(position).getName();
        String thumbnail = data.get(position).getThumbnailUrl();

        communicationInterface.getLocalPlaylist(id, name);

    }

    @Override
    public void onItemLongClicked(int position) {

        final LocalPlaylistOptionsDialog localPlaylistOptionsDialog = new LocalPlaylistOptionsDialog(data.get(position).getName());
        localPlaylistOptionsDialog.show(getChildFragmentManager(), localPlaylistOptionsDialog.getTag());
        localPlaylistOptionsDialog.setOnActionListener(new LocalPlaylistOptionsDialog.OnActionListener() {
            @Override
            public void onRemove() {
                localPlaylistOptionsDialog.dismiss();
                ConfirmRemoveMyPlaylistDialog confirmRemoveMyPlaylistDialog = new ConfirmRemoveMyPlaylistDialog();
                confirmRemoveMyPlaylistDialog.show(getChildFragmentManager(), confirmRemoveMyPlaylistDialog.getTag());
                confirmRemoveMyPlaylistDialog.setOnActionListener(new ConfirmRemoveMyPlaylistDialog.OnActionListener() {
                    @Override
                    public void onCancel() {
                        confirmRemoveMyPlaylistDialog.dismiss();
                    }

                    @Override
                    public void onOk() {
                        AppDatabase.getInstance(getActivity()).getPlaylistDao().delete(data.get(position));
                        data.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, data.size());
                        confirmRemoveMyPlaylistDialog.dismiss();
                    }
                });
            }

            @Override
            public void onRename() {
                SaveMyPlaylistDialog saveMyPlaylistDialog = new SaveMyPlaylistDialog(data.get(position).getName());
                saveMyPlaylistDialog.show(getChildFragmentManager(), saveMyPlaylistDialog.getTag());
                saveMyPlaylistDialog.setOnActionListener(new SaveMyPlaylistDialog.OnActionListener() {
                    @Override
                    public void onCancel() {
                        saveMyPlaylistDialog.dismiss();
                    }

                    @Override
                    public void onOk(String str) {
                        data.get(position).setName(str);
                        AppDatabase.getInstance(getActivity()).getPlaylistDao().update(data.get(position));
                        getData();
                        saveMyPlaylistDialog.dismiss();
                        localPlaylistOptionsDialog.dismiss();
                    }
                });
            }
        });
    }
}
