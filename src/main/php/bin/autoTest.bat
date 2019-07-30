cd ..
javac -d "./storage/generator" -encoding UTF-8 -classpath "./storage/generator/*;./storage/generator;." .\resources\generator\templates\*
java -classpath "./storage/generator/*;./storage/generator;./resources/generator;." com.kamicloud.generator.AutoTest
pause
