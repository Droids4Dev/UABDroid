/*
   Copyright 2012 Ruben Serrano, Joan Fuentes

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.uab.deic.uabdroid;

import java.util.ArrayList;
import java.util.Locale;

import org.uab.deic.uabdroid.adapters.DatabaseAdapter;
import org.uab.deic.uabdroid.utils.Examination;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 
 * @author Joan Fuentes
 *
 */
public class ExaminationActivity extends BaseActivity
{
	private static final float MARK_PERCENT_POINT_CUTOFF = 50.0f;
	private static final int NUMBER_OF_QUESTIONS = 10;
	
	private ArrayList<Examination> mExamList;
	private int[] mCorrectAnswers;
	private int[] mSelectedAnswers;

	private DatabaseAdapter mDatabaseAdapter = null;
	private int mNumberOfQuestionLoaded;

	private RadioGroup mRadioGroupAnswers;
	private TextView mTextViewQuestionTitle;
	private TextView mTextViewQuestion;
	private Button mButtonPrevious;
	private Button mButtonNext;
	
	@Override
	public void onCreate(Bundle _savedInstanceState) 
	{
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.examination);
		
		setActionBarTitleOrDefault(R.string.app_text_examination);

        // Get references to the child controls
        mTextViewQuestionTitle = (TextView)findViewById(R.id.txtQuestionTitle);
        mTextViewQuestion = (TextView)findViewById(R.id.txtQuestion);
		mRadioGroupAnswers = (RadioGroup)findViewById(R.id.radioGroupAnswers);
		mButtonPrevious = (Button)findViewById(R.id.bttnPrevious);
		mButtonNext = (Button)findViewById(R.id.bttnNext);
		
		mSelectedAnswers = new int[NUMBER_OF_QUESTIONS];

		// ** INITIALIZE SOME VALUES AND CONFIGURATIONS **
		
		// We are in the first question, mButtonPrevious is hidden. 
		mButtonPrevious.setVisibility(View.INVISIBLE);
		
		// If we are in the last question, button "Next" changes to "Finish"
		if (mNumberOfQuestionLoaded == NUMBER_OF_QUESTIONS-1) mButtonNext.setText(getResources().getString(R.string.button_result).toString());
		
		// Open the Db Adapter
		mDatabaseAdapter = new DatabaseAdapter(this);
		mDatabaseAdapter.open();
		
        // Recover the questions and answers.
        // It's important to initialize first an ArrayList<Examination> and an int[].
        mExamList = new ArrayList<Examination>();
		mCorrectAnswers = new int[NUMBER_OF_QUESTIONS];
		
		if (mDatabaseAdapter.getQuestionsAndAnswers(mExamList, mCorrectAnswers, NUMBER_OF_QUESTIONS))
		{
			mNumberOfQuestionLoaded=0;
			loadQuestion(mNumberOfQuestionLoaded);
		}
		else 
		{
			(Toast.makeText(this, "ERROR IN DB. PLEASE REINSTALL THE APPLICATION",1)).show();			
		}
		
		// Set the listeners
		mButtonNext.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mButtonNextAction();
			}
		});

		mButtonPrevious.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mButtonPreviousAction();
			}
		});
		
	}

	@Override
	public void onConfigurationChanged(Configuration _newConfiguration) 
	{
	  super.onConfigurationChanged(_newConfiguration);
	  //setContentView(R.layout.examination);
	  //if(loadQuestion(mNumberOfQuestionLoaded)) ;
	}

	
	@Override
    public void onDestroy()
    {
		mDatabaseAdapter.close();
    	super.onDestroy();
    }	
	

    /**
     * TODO
     */
	public boolean loadQuestion(int _numOfQuestionToLoad) 
	{
		try 
		{
			String locale = Locale.getDefault().toString(); 
			
			//Recover the requested question-answers.
			Examination questionAndAnswers = mExamList.get(_numOfQuestionToLoad);
			
			// Set the values on the fields of the layout.
			mTextViewQuestionTitle.setText(getResources().getString(R.string.examination_question_title) + " " + (_numOfQuestionToLoad + 1));

			if(locale.compareTo("ca_ES")==0)
			{
				mTextViewQuestion.setText(questionAndAnswers.getQuestion("ca_ES"));
	        }
			else
			{
				mTextViewQuestion.setText(questionAndAnswers.getQuestion("es_ES"));
	        }
			
			// Clear the selected radioButton.
			mRadioGroupAnswers.clearCheck();
			
			// For a undetermined number of radiobuttons better use next loop to load the
			// text of every radioButton:
			for (int i = 0; i < mRadioGroupAnswers.getChildCount(); i++) 
			{
		        ((RadioButton) mRadioGroupAnswers.getChildAt(i)).setText(questionAndAnswers.getAnswer(i));
		    }
			
			mNumberOfQuestionLoaded = _numOfQuestionToLoad;
		} 
		catch (Exception e) 
		{
			return false;
		}
		return true;
	}

	
    /**
     * TODO
     */
	public void mButtonNextAction(){
		// Save the selected answer.
		// If some answer is not selected, is saved an 0 value
		// If user is so stupid to not answer... bad luck :)
		int selectedAnswer;
		switch (mRadioGroupAnswers.getCheckedRadioButtonId()) 
		{
			case R.id.radioAnswer1:
				selectedAnswer = 1;
				break;
			case R.id.radioAnswer2:
				selectedAnswer = 2;
				break;
			case R.id.radioAnswer3:
				selectedAnswer = 3;
				break;
			default:
				selectedAnswer = 0;
				break;
		}
		mSelectedAnswers[mNumberOfQuestionLoaded] = selectedAnswer;
		
		// if we are in the last question, next step is the result of test
		if (mNumberOfQuestionLoaded == NUMBER_OF_QUESTIONS-1)
		{
			showResult();
		}
		else
		{
			// Load the next answer 
			if(loadQuestion(mNumberOfQuestionLoaded+1))
			{
				// if it's loaded the last question, we change the text "Next" by "Finish".
				if (mNumberOfQuestionLoaded == NUMBER_OF_QUESTIONS-1)
				{
					mButtonNext.setText(getResources().getString(R.string.button_result).toString());
				}
				// We make visible the "Previous" button.
				mButtonPrevious.setVisibility(View.VISIBLE);
				// if user previously answered, the selected option is recovered.
				if(mSelectedAnswers[mNumberOfQuestionLoaded] != 0) 
				{
					((RadioButton) mRadioGroupAnswers.getChildAt(mSelectedAnswers[mNumberOfQuestionLoaded]-1)).setChecked(true);
				}
			}
		}	
	}
	
	
	public void mButtonPreviousAction()
	{
		// if we are in the first question, mButtonPrevious is hidden.
		// Load the next answer 
		if(loadQuestion(mNumberOfQuestionLoaded-1))
		{
			if (mNumberOfQuestionLoaded == 0)
			{
				mButtonPrevious.setVisibility(View.INVISIBLE);
			}
			mButtonNext.setText(getResources().getString(R.string.button_next).toString());
			if(mSelectedAnswers[mNumberOfQuestionLoaded] != 0) 
			{
				((RadioButton) mRadioGroupAnswers.getChildAt(mSelectedAnswers[mNumberOfQuestionLoaded]-1)).setChecked(true);
			}
		}		
	}
	
	public void showResult() 
	{
		float validAnswers = 0;
		boolean pass;
		float qualification;
		
		for (int i = 0; i < NUMBER_OF_QUESTIONS; i++) 
		{
			if(mCorrectAnswers[i]==mSelectedAnswers[i]) validAnswers++;
		}
		
		qualification = (validAnswers/NUMBER_OF_QUESTIONS)*100;

		if((int) Float.compare(qualification, MARK_PERCENT_POINT_CUTOFF) >= 0)
		{
			pass = true;
		}
		else
		{
			pass = false;
		}
		
		// Delete the content of the layout (all views in "linearLayoutOfContent")
		// We create a RelativeLayout and put inside an ImageView and 2 TextViews with 
		// the result of the test.
		LinearLayout linearLayoutOfContent = (LinearLayout) findViewById(R.id.linearLayoutOfContent);
		linearLayoutOfContent.removeAllViews();
		
		RelativeLayout relativeLayoutResult = new RelativeLayout(this);
		relativeLayoutResult.setId(1);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//params.addRule(RelativeLayout.ALIGN_PARENT_TOP);		
		relativeLayoutResult.setLayoutParams(params);
		
		RelativeLayout.LayoutParams relativeParamsImageResult = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		relativeParamsImageResult.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		//relativeParamsImageResult.addRule(RelativeLayout.CENTER_IN_PARENT);		
		//relativeParamsImageResult.addRule(RelativeLayout.ALIGN_TOP);

		ImageView imageViewResult= new ImageView(this);
		imageViewResult.setImageDrawable(getResources().getDrawable(pass ? R.drawable.pass : R.drawable.fail));
		imageViewResult.setId(2);
		imageViewResult.setScaleType(ImageView.ScaleType.CENTER_CROP);
		relativeLayoutResult.addView(imageViewResult,relativeParamsImageResult);

		TextView textViewResult1 = new TextView(this);
		textViewResult1.setText(getResources().getString((pass ? R.string.pass_1 : R.string.fail_1)));
		textViewResult1.setTextColor(Color.BLACK);
		textViewResult1.setTextSize(35);
		textViewResult1.setTypeface(Typeface.DEFAULT_BOLD);

		RelativeLayout.LayoutParams relativeParamsTxtResult1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParamsTxtResult1.addRule(RelativeLayout.ALIGN_TOP,imageViewResult.getId());
		relativeParamsTxtResult1.addRule(RelativeLayout.CENTER_IN_PARENT,imageViewResult.getId());
		relativeLayoutResult.addView(textViewResult1,relativeParamsTxtResult1);

		TextView textViewResult2 = new TextView(this);
		textViewResult2.setText(getResources().getString((pass ? R.string.pass_2 : R.string.fail_2)));
		textViewResult2.setTextColor(Color.BLACK);
		textViewResult2.setTextSize(30);
		textViewResult2.setLines(1);
		textViewResult2.setTypeface(Typeface.DEFAULT_BOLD);
		
		RelativeLayout.LayoutParams relativeParamsTxtResult2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		relativeParamsTxtResult2.addRule(RelativeLayout.ALIGN_BOTTOM,imageViewResult.getId());
		relativeParamsTxtResult2.addRule(RelativeLayout.CENTER_IN_PARENT,imageViewResult.getId());
		relativeLayoutResult.addView(textViewResult2,relativeParamsTxtResult2);
		
		linearLayoutOfContent.addView(relativeLayoutResult);
		
	}
	

}
