#include "podaci_Reader.h"
#include <iostream>
#include <regex>
#include <string>
#include <unordered_map>
#include <memory>
#include <vector>



JNIEXPORT jobjectArray JNICALL Java_podaci_Reader_filterFuncNumberOfContestants
(JNIEnv* env, jobject Reader, jobject filterr, jobjectArray SYTM) {
	// there we will count competitors which passed filters for every country for all olympic games
	std::shared_ptr<std::unordered_map<std::string, int>> countryAndNum = std::make_shared<std::unordered_map<std::string, int>>();

	// getting methods for filter
	jclass readerClass = env->FindClass("podaci/Reader");
	// ovo sam dodao
	jfieldID filterID = env->GetStaticFieldID(readerClass, "filter", "Lpodaci/Filter;");
	jobject filter = env->GetStaticObjectField(readerClass, filterID);

	jclass filterClass = env->FindClass("podaci/Filter");
	jmethodID getSportID = env->GetMethodID(filterClass, "getSport", "()Ljava/lang/String;");
	jmethodID getYearID = env->GetMethodID(filterClass, "getYear", "()Ljava/lang/String;");
	jmethodID getIndividualID = env->GetMethodID(filterClass, "getIndividual", "()Ljava/lang/String;");
	jmethodID getMedalID = env->GetMethodID(filterClass, "getMedal", "()Ljava/lang/String;");

	// parsing elements from jobjectArray
	jint rows0 = env->GetArrayLength(SYTM);

	int brojac = 0;
	for (int r0 = 0; r0 < rows0; r0++) {
		//std::cout << r << '\n';
		// array with countries
		jobjectArray countries = (jobjectArray)(env->GetObjectArrayElement(SYTM, r0));
		jint rows1 = env->GetArrayLength(countries);
		for (int r1 = 0; r1 < rows1; r1++) {
			jobjectArray forOneCountry = (jobjectArray)(env->GetObjectArrayElement(countries, r1));
			jint columns = env->GetArrayLength(forOneCountry);
			// we can take country
			jstring countryy = (jstring)(env->GetObjectArrayElement(forOneCountry, 0));
			const char* country = env->GetStringUTFChars(countryy, 0);
			countryAndNum->insert(std::pair<std::string, int>(country, 0));
			// start by 1 because we took country
			for (int c = 1; c < columns; c++) {
				jstring s = (jstring)(env->GetObjectArrayElement(forOneCountry, c));
				// this method returns const char*
				const char* sss = env->GetStringUTFChars(s, 0);
				std::string ss = sss;

				// now parsing string and check filters
				std::smatch result;
				std::regex re("([^~]+)~([^~]+)~([^~]+)~([^~]+)");
				std::regex_match(ss, result, re);

				bool check; // i will need it for every filter
				// check for sport
				jstring sp = (jstring)env->CallObjectMethod(filter, getSportID, 0);
				const char* sportC = env->GetStringUTFChars(sp, 0);
				std::string sport = sportC;
				if (sport != "") {
					std::regex re(".*" + sport + ".*");
					std::string sports = result.str(1);
					std::smatch result;
					check = std::regex_match(sports, result, re);
					env->ReleaseStringUTFChars(sp, sportC);
					if (!check) continue;
				}
				else {
					env->ReleaseStringUTFChars(sp, sportC);
				}

				// check for year
				jstring ye = (jstring)env->CallObjectMethod(filter, getYearID);
				const char* yearC = env->GetStringUTFChars(ye, 0);
				std::string year = yearC;
				if (year != "") {
					std::regex re(".*" + year + ".*");
					std::string yearr = result.str(2);
					std::smatch result;
					check = std::regex_match(yearr, result, re);
					env->ReleaseStringUTFChars(ye, yearC);
					if (!check) continue;
				}
				else {
					env->ReleaseStringUTFChars(ye, yearC);
				}

				// check for type( individual - true, team - false )
				jstring ty = (jstring)env->CallObjectMethod(filter, getIndividualID);
				const char* typeC = env->GetStringUTFChars(ty, 0);
				std::string type = typeC;
				if (type != "") {
					std::regex re(".*" + type + ".*");
					std::string typee = result.str(3);
					std::smatch result;
					check = std::regex_match(typee, result, re);
					env->ReleaseStringUTFChars(ty, typeC);
					if (!check) continue;
				}
				else {
					env->ReleaseStringUTFChars(ty, typeC);
				}

				// check for medal
				jstring me = (jstring)env->CallObjectMethod(filter, getMedalID);
				const char* medalC = env->GetStringUTFChars(me, 0);
				std::string medal = medalC;

				//std::cout << "OVDEEEEEE\n";
				if (medal != "") {
					std::regex re(".*" + medal + ".*");
					std::string medall = result.str(4);
					std::smatch result;
					check = std::regex_match(medall, result, re);
					if (!check) continue;
					std::regex ree(".*" + medal + "([0-9]+).*");
					std::string medalAndNum = medall;
					check = std::regex_match(medalAndNum, result, ree);

					(*countryAndNum)[country] += std::stoi(result.str(1));
					env->ReleaseStringUTFChars(me, medalC);
					continue;
					
				}
				else {
					env->ReleaseStringUTFChars(me, medalC);
				}

				// we passed all filters
				(*countryAndNum)[country]++;

				// realeasing native heap
				env->ReleaseStringUTFChars(s, sss);

			}

		}
	}

	// we will return this array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray arrayForReturning = env->NewObjectArray(countryAndNum->size(), stringClass, env->NewStringUTF(""));

	// add element to returning array
	int ind = 0;
	for (auto pair : *countryAndNum) {
		std::string tmp = pair.first + "!";
		tmp += std::to_string(pair.second);
		//std::cout << tmp << '\n';
		env->SetObjectArrayElement(arrayForReturning, ind++, env->NewStringUTF(tmp.c_str()));
	}

	return arrayForReturning;

	//return 0;

}

JNIEXPORT jobjectArray JNICALL Java_podaci_Reader_filterForDisciplines
(JNIEnv* env, jobject Reader, jobjectArray numberOfDisciplinesPerYear, jint yearFrom, jint yearTo) {
	
	// tmp vector for adding OlympicGames which passed range of years
	std::vector<std::string> tmp;
	for (int i = 0; i < env->GetArrayLength(numberOfDisciplinesPerYear); i++) {
		jstring s = (jstring)(env->GetObjectArrayElement(numberOfDisciplinesPerYear, i));
		// this method returns const char*
		const char* sss = env->GetStringUTFChars(s, 0);
		std::string ss = sss;
		std::smatch result;
		std::regex re("([^~]+)~([^~]+)~([^~]+)");
		std::regex_match(ss, result, re);

		// filtering
		if (atoi(result.str(1).c_str()) >= yearFrom && atoi(result.str(1).c_str()) <= yearTo)
			tmp.push_back(ss);

		env->ReleaseStringUTFChars(s, sss);
	}

	// we will return this array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray arrayForReturning = env->NewObjectArray(env->GetArrayLength(numberOfDisciplinesPerYear), stringClass, env->NewStringUTF(""));

	int ind = 0;
	for (auto s : tmp) {
		env->SetObjectArrayElement(arrayForReturning, ind++, env->NewStringUTF(s.c_str()));
	}

	return arrayForReturning;
		
}

JNIEXPORT jobjectArray JNICALL Java_podaci_Reader_filterForHeightOrWeight
(JNIEnv* env, jobject Reader, jobjectArray competitorsPerYear, jint yearFrom, jint yearTo) {

	// tmp vector for adding OlympicGames which passed range of years
	std::vector<std::string> tmp;
	for (int r = 0; r < env->GetArrayLength(competitorsPerYear); r++) {
		jobjectArray heightsOrWeights = (jobjectArray)env->GetObjectArrayElement(competitorsPerYear, r);
		// iterate through all competitors
		int sum = 0;
		// i need this string to concatenate with avrg height
		std::string yearAndSeas;
		for (int c = 0; c < env->GetArrayLength(heightsOrWeights); c++) {
			// checking is year in range
			if (c == 0) {
				jstring yearAndSeason = (jstring)env->GetObjectArrayElement(heightsOrWeights, c);
				const char* sss = env->GetStringUTFChars(yearAndSeason, 0);
				std::string ss = sss;
				std::smatch result;
				std::regex re("([^~]+)~([^~]+)");
				std::regex_match(ss, result, re);
				if (atoi(result.str(1).c_str()) >= yearFrom && atoi(result.str(1).c_str()) <= yearTo) {
					// i will just add Year and season, avrg height/weight i will calculate in other iterates
					yearAndSeas = ss;
					env->ReleaseStringUTFChars(yearAndSeason, sss);
					continue;
				}
				else {
					env->ReleaseStringUTFChars(yearAndSeason, sss);
					break;
				}
			}
			jstring heightOrWeight = (jstring)env->GetObjectArrayElement(heightsOrWeights, c);
			const char* sss = env->GetStringUTFChars(heightOrWeight, 0);
			std::string ss = sss;
			sum += atoi(ss.c_str());

			env->ReleaseStringUTFChars(heightOrWeight, sss);
		}
		// - 1 because first one is for year and season
		if (yearAndSeas != "")
			tmp.push_back(yearAndSeas + "~" + std::to_string(sum * 1. / (env->GetArrayLength(heightsOrWeights) - 1)));

	}
	// we will return this array
	jclass stringClass = env->FindClass("java/lang/String");
	jobjectArray arrayForReturning = env->NewObjectArray(env->GetArrayLength(competitorsPerYear), stringClass, env->NewStringUTF(""));

	int ind = 0;
	for (auto s : tmp) {
		env->SetObjectArrayElement(arrayForReturning, ind++, env->NewStringUTF(s.c_str()));
	}

	return arrayForReturning;
}