package com.scholarscore.models.survey.answer;

import com.scholarscore.models.survey.question.SurveyOpenResponseQuestion;
import com.scholarscore.models.survey.SurveyQuestionType;

/**
 * Created by markroper on 12/16/15.
 */
public class OpenAnswer extends QuestionAnswer<String, SurveyOpenResponseQuestion> {
    public OpenAnswer() {
        this.type = SurveyQuestionType.OPEN_RESPONSE;
    }
}
