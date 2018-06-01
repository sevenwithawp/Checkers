package com.example.ros66.testt;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;




public class MainActivity extends Activity {


	GridView gridView;
	CheckersBoard cb = new CheckersBoard();
    String capturedCheckerPosition;
	int checkerIsCaptured = 0;
	int hardCodedServerMove = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int h = metrics.heightPixels;
		int w = metrics.widthPixels;
		System.out.println(h);
		System.out.println(w);
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        CheckersPiece all_pieces[] = new CheckersPiece[24];
		for (int i = 0; i < 12; i++) {
			all_pieces[i] = cb.dark_pieces[i];
			all_pieces[i+12] = cb.light_pieces[i];
		}
		String myvec[];
		myvec = cb.vec_string();
		gridView = findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this, myvec));
		Context context = getApplicationContext();
		CharSequence text = "White Player starts!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

    public void pressed(View view) {
		System.out.println("pressed");
		EditText e = findViewById(R.id.editText1);
		System.out.println(e.getText().toString());
		if (e.getText().toString().length() >= 1) {

			CheckersMove cm = new CheckersMove(e.getText().toString());

			cb.board_move_and_capture(cm);
			String myvec[];
			myvec = cb.vec_string();
			gridView = findViewById(R.id.gridview);
			gridView.setAdapter(new ImageAdapter(this, myvec));
		}
	}

	public void moveCheckerOnBoard(String fromPosition, String toPosition) {
		String move = fromPosition + "-" + toPosition;
		// Add the move validation functionality
		int moveStatus = cb.board_move_and_capture(new CheckersMove(move));
		String boardPositions[];
		boardPositions = cb.vec_string();
		gridView = findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this, boardPositions));
		cb.board_print();

	}

	public void image_pressed(View view) {

		if (checkerIsCaptured == 0) {
			capturedCheckerPosition = (String) view.getTag();
			checkerIsCaptured = 1;
		} else {
			moveCheckerOnBoard(capturedCheckerPosition, (String) view.getTag());
			checkerIsCaptured = 0;
			// JsonObjectRequest.java
			String URL ="https://robo-hand.appspot.com/move?number=" + String.valueOf(hardCodedServerMove);
			System.out.println(URL);
			hardCodedServerMove = hardCodedServerMove + 1;
			RequestQueue queue = Volley.newRequestQueue(this);
			//RequestQueue mRequestQueue = Volley.newRequestQueue(this);
			JsonObjectRequest request = new JsonObjectRequest(

					URL,
					null,
					new com.android.volley.Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							if (response != null) {
								String status = response.optString("status");
								if (status.isEmpty()) {
									String fromPosition = response.optString("from");
									String toPosition = response.optString("to");
									moveCheckerOnBoard(fromPosition, toPosition);
								} else {
									// Show the return to menu screen
									System.out.println("status: " + status);
								}

							}
						}
					},
					new com.android.volley.Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i("ResponseError: ", error.toString());
						}
					}

			);
			queue.add(request);
			queue.start();

		}

	}
	public void leave(View view)
	{
		this.finish();
	}
	
}
