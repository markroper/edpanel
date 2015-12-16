package com.scholarscore.models.survey.answer;

import com.scholarscore.models.survey.question.SurveyBooleanQuestion;
import com.scholarscore.models.survey.SurveyQuestionType;

/**
 * Created by markroper on 12/16/15.
 */
public class BooleanAnswer extends QuestionAnswer<Boolean, SurveyBooleanQuestion> {
    public BooleanAnswer() {
        this.type = SurveyQuestionType.TRUE_FALSE;
    }
}
