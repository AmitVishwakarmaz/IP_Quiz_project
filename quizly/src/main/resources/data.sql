INSERT INTO quiz_entity (title, category, time_limit_minutes, instructor_id)
SELECT 'Everyday Mathematics', 'Mathematics', 15, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_entity WHERE title = 'Everyday Mathematics'
);

INSERT INTO quiz_entity (title, category, time_limit_minutes, instructor_id)
SELECT 'Our Planet', 'Science', 15, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_entity WHERE title = 'Our Planet'
);

INSERT INTO quiz_entity (title, category, time_limit_minutes, instructor_id)
SELECT 'World Explorer', 'Geography', 15, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_entity WHERE title = 'World Explorer'
);

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'What is 12 multiplied by 8?', '86', '92', '96', '108', 'C', 'Arithmetic', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'Everyday Mathematics'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'What is 12 multiplied by 8?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'Which fraction is equal to one half?', '2/3', '3/6', '4/6', '5/8', 'B', 'Fractions', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'Everyday Mathematics'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'Which fraction is equal to one half?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'What is the perimeter of a square with side length 5?', '10', '15', '20', '25', 'C', 'Geometry', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'Everyday Mathematics'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'What is the perimeter of a square with side length 5?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'Which gas do plants absorb from the air?', 'Oxygen', 'Carbon dioxide', 'Nitrogen', 'Hydrogen', 'B', 'Biology', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'Our Planet'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'Which gas do plants absorb from the air?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'What is the closest star to Earth?', 'Sirius', 'The Sun', 'Polaris', 'Alpha Centauri', 'B', 'Astronomy', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'Our Planet'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'What is the closest star to Earth?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'At what temperature does water freeze in Celsius?', '0 degrees', '10 degrees', '32 degrees', '100 degrees', 'A', 'Physics', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'Our Planet'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'At what temperature does water freeze in Celsius?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'Which is the largest ocean on Earth?', 'Atlantic Ocean', 'Indian Ocean', 'Arctic Ocean', 'Pacific Ocean', 'D', 'Oceans', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'World Explorer'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'Which is the largest ocean on Earth?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'What is the capital city of Japan?', 'Seoul', 'Tokyo', 'Bangkok', 'Beijing', 'B', 'Capitals', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'World Explorer'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'What is the capital city of Japan?'
  );

INSERT INTO question_entity
    (question_text, optiona, optionb, optionc, optiond, correct_option, category, quiz_id)
SELECT 'On which continent is the Sahara Desert?', 'Asia', 'South America', 'Africa', 'Australia', 'C', 'Continents', quiz.id
FROM quiz_entity quiz
WHERE quiz.title = 'World Explorer'
  AND NOT EXISTS (
      SELECT 1 FROM question_entity question
      WHERE question.quiz_id = quiz.id AND question.question_text = 'On which continent is the Sahara Desert?'
  );
