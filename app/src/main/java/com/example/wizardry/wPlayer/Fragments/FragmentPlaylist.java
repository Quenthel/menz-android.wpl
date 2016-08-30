package com.example.wizardry.wPlayer.Fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.wizardry.wPlayer.Activities.PlayListActivity;
import com.example.wizardry.wPlayer.Adapters.PlayListAdapterRec;
import com.example.wizardry.wPlayer.Helpers.ItemClickSupport;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.MusicRetriever;
import com.example.wizardry.wPlayer.Retrievers.PlaylistRetriever;
import com.example.wizardry.wPlayer.Retrievers.RetrieverHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizardry on 19/04/2016.
 */
public class FragmentPlaylist extends Fragment {
    String currentPlayList = "";
    String currentPlayListPath = "";
    PlayListAdapterRec adapter;
    View sd;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = new MenuInflater(getContext());
        menu.setHeaderIcon(R.drawable.ic_action_edit);
        sd = v;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TextView tx = (TextView) info.targetView.findViewById(R.id.label);
        TextView tx2 = (TextView) info.targetView.findViewById(R.id.data);
        currentPlayListPath = tx2.getText().toString();
        currentPlayList = tx.getText().toString();
        menu.setHeaderTitle(currentPlayList);
        mi.inflate(R.menu.menu_options_list, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        final ArrayAdapter<PlaylistRetriever.Item> adapterSon;
        ContentResolver cr = getActivity().getContentResolver();
        final List<PlaylistRetriever.Item> l = PlaylistRetriever.loadingSongs(cr);
        ImageButton ddd = (ImageButton) view.findViewById(R.id.zzzz);


        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.listViewPlayList);
        // Initialize contacts
        // Create adapter passing in the sample user data
        adapter = new PlayListAdapterRec(getContext(), l);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setHasFixedSize(true);
        ItemClickSupport.addTo(rvContacts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                PlaylistRetriever.Item actual = l.get(position);
                ArrayList<MusicRetriever.Item> a = new PlaylistRetriever(getContentResolver()).aBlazing(actual.getId());
                Intent i = new Intent(getActivity(), PlayListActivity.class);
                i.putExtra("name", actual.getName());
                i.putExtra("songs", a);
                i.putExtra("playpath", actual.getWhatever());
                getActivity().startActivity(i);            }
        });

        ddd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getContext());
                final View promptsView = li.inflate(R.layout.add, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e("Adding play:", userInput.getText().toString());
                                RetrieverHelper.createPlayList(getActivity().getContentResolver(), userInput.getText().toString());
                                Snackbar.make(promptsView, "added", Snackbar.LENGTH_SHORT);
                                adapter.notifyItemInserted(adapter.getItemCount());
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        /*
        ListView lv2 = (ListView) view.findViewById(R.id.listViewPlayList);
        adapterSon = new PlayListAdapter(getActivity(), new ArrayList<PlaylistRetriever.Item>());
        lv2.setAdapter(adapterSon);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistRetriever.Item actual = l.get(position);
                ArrayList<MusicRetriever.Item> a = new PlaylistRetriever(getContentResolver()).aBlazing(actual.getId());
                Intent i = new Intent(getActivity(), PlayListActivity.class);
                i.putExtra("name", actual.getName());
                i.putExtra("songs", a);
                i.putExtra("playpath", actual.getWhatever());
                getActivity().startActivity(i);
            }
        });

        for (PlaylistRetriever.Item i : l) {
            adapterSon.add(i);
        }

        registerForContextMenu(lv2);*/
        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.playtop:
                Log.e("OPTION", "PlayTOP");
                break;*/
            case R.id.deletepla:
                Log.e("OPCION", "DELETE " + currentPlayListPath);
                RetrieverHelper.deletePlayList(getActivity().getContentResolver(), RetrieverHelper.getPlaylist(getActivity().getContentResolver(), currentPlayList));
                // this.getActivity().recreate();
                adapter.notifyDataSetChanged();

                break;
            case R.id.edit:
                LayoutInflater li = LayoutInflater.from(getContext());
                final View promptsView = li.inflate(R.layout.add, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.e("Rename playlist:", userInput.getText().toString());
                                        RetrieverHelper.renamePlaylist(getActivity().getContentResolver(), RetrieverHelper.getPlaylist(getActivity().getContentResolver(), currentPlayList), userInput.getText().toString());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }
}
