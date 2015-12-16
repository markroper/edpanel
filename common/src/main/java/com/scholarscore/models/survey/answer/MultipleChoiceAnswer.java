package com.scholarscore.models.survey.answer;

import com.scholarscore.models.survey.question.SurveyMultipleChoiceQuestion;
import com.scholarscore.models.survey.SurveyQuestionType;

/**
 * Created by markroper on 12/16/15.
 */
public class MultipleChoiceAnswer extends QuestionAnswer<Integer, SurveyMultipleChoiceQuestion> {
    public MultipleChoiceAnswer() {
        this.type = SurveyQuestionType.MULTIPLE_CHOICE;
    }
}
