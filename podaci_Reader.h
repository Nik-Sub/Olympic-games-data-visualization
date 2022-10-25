/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class podaci_Reader */

#ifndef _Included_podaci_Reader
#define _Included_podaci_Reader
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     podaci_Reader
 * Method:    filterFuncNumberOfContestants
 * Signature: (Lpodaci/Filter;[[[Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_podaci_Reader_filterFuncNumberOfContestants
  (JNIEnv *, jobject, jobject, jobjectArray);

/*
 * Class:     podaci_Reader
 * Method:    filterForDisciplines
 * Signature: ([Ljava/lang/String;II)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_podaci_Reader_filterForDisciplines
  (JNIEnv *, jobject, jobjectArray, jint, jint);

/*
 * Class:     podaci_Reader
 * Method:    filterForHeightOrWeight
 * Signature: ([[Ljava/lang/String;II)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_podaci_Reader_filterForHeightOrWeight
  (JNIEnv *, jobject, jobjectArray, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
