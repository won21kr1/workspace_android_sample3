package com.cardsui.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class MainActivity extends Activity {

	private CardUI mCardView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);

		// add AndroidViews Cards
		mCardView.addCard(new MyCard("Get the CardsUI view"));
		mCardView.addCardToLastStack(new MyCard("for Android at"));
		MyCard androidViewsCard = new MyCard("www.androidviews.net");
		androidViewsCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.androidviews.net/"));
				startActivity(intent);

			}
		});
		mCardView.addCardToLastStack(androidViewsCard);

		// add one card, and then add another one to the last stack.
		mCardView.addCard(new MyCard("2 cards"));
		mCardView.addCardToLastStack(new MyCard("2 cards"));

		// add one card
		mCardView.addCard(new MyCard("1 card"));

		// create a stack
		CardStack stack = new CardStack();
		stack.setTitle("title test");

		// add 3 cards to stack
		stack.add(new MyCard("3 cards"));
		stack.add(new MyCard("3 cards"));
		stack.add(new MyCard("3 cards"));

		// add stack to cardView
		mCardView.addStack(stack);

		// draw cards
		mCardView.refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
