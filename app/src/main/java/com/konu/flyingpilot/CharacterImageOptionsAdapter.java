package com.konu.flyingpilot;

import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import models.CharacterOption;

public class CharacterImageOptionsAdapter extends RecyclerView.Adapter<CharacterImageOptionsAdapter.CharacterViewHolder> {

    private ArrayList<CharacterOption> playerOptionArrayList = new ArrayList<>();
    private PickCharacterDialog dialog;

    public CharacterImageOptionsAdapter(ArrayList<CharacterOption> horizontalList, PickCharacterDialog dialog) {
        this.playerOptionArrayList = horizontalList;
        this.dialog = dialog;
    }

    @Override
    public int getItemCount() {
        return playerOptionArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return  super.getItemViewType(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final CharacterViewHolder holder, final int position) {
        final CharacterOption model = playerOptionArrayList.get(position);
        CharacterViewHolder cHolder = (CharacterViewHolder) holder;

        //Controlling each Character UI based on the model

        cHolder.imageView.setImageResource(model.getImagePath());
        cHolder.imageView.getLayoutParams().height = model.getHeight();
        cHolder.imageView.getLayoutParams().width = model.getHeight();


        if (model.isOwned()) {//If owned show select or selected buttong
            cHolder.priceTV.setVisibility(View.INVISIBLE);
            cHolder.selectBtn.setText(model.isSelected() ? R.string.selected : R.string.select);
            cHolder.selectBtn.getBackground().setColorFilter((dialog.getContext().getColor(model.isSelected() ? R.color.green : R.color.colorPrimary)), PorterDuff.Mode.MULTIPLY);
        } else {//else show buy if possible
            cHolder.priceTV.setVisibility(View.VISIBLE);
            String price = NumberFormat.getNumberInstance(Locale.US).format(dialog.PRICE);
            cHolder.priceTV.setText(dialog.getContext().getResources().getString(R.string.coins) + ": " + price);

            cHolder.selectBtn.setText(dialog.canBuy ? R.string.buy : R.string.locked);
            cHolder.selectBtn.getBackground().setColorFilter(dialog.getContext().getColor(dialog.canBuy ?R.color.colorPrimary : R.color.red), PorterDuff.Mode.MULTIPLY);
        }

        cHolder.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.isSelected()) return;

                if(!model.isOwned()){
                    if(dialog.canBuy){
                        dialog.buyCharacter(model.getId());
                    }else{
                        return;
                    }
                }
                for (int i = 0; i < playerOptionArrayList.size(); i++) {
                    CharacterOption tmp = playerOptionArrayList.get(i);
                    tmp.setSelected(model.getId() == tmp.getId()); //Check if the model pressed and change to selected if it is
                    if(!tmp.isOwned()&&tmp.getId() == model.getId()){//If just bought the character change to owned
                        tmp.setOwned(true);
                    }
                }

                //Updating UI
                notifyDataSetChanged();
                dialog.setPlayer(model.getId());
            }
        });

    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        Button selectBtn;
        TextView priceTV;

        private CharacterViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            selectBtn = view.findViewById(R.id.select_player_btn);
            priceTV = view.findViewById(R.id.coins_price);
        }
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_choice, parent, false);
        return new CharacterViewHolder(itemView);
    }
}
