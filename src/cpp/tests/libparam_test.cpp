#include <iostream>
#include <gtest/gtest.h>
#include "../paramtuner.h"


TEST(ParamTunerTest, checkValidFileLoad)
{
  EXPECT_EQ( ParamTuner::load("settings.xml"), 0 ) ;
}

TEST(ParamTunerTest, checkSlowDoubleDifferentFilesLoad_OpenIssue)
{
  sleep(1);
  EXPECT_EQ( ParamTuner::load("settings1.xml"), 0 ) ;
  sleep(1);
  EXPECT_EQ( ParamTuner::load("settings2.xml"), 0 ) ;
}


TEST(ParamTunerTest, checkDoubleDifferentFilesLoad_OpenIssue)
{
  EXPECT_EQ( ParamTuner::load("settings1.xml"), 0 ) ;
  EXPECT_EQ( ParamTuner::load("settings2.xml"), 0 ) ;
}

TEST(ParamTunerTest, checkDoubleLoad_OpenIssue)
{
  EXPECT_EQ( ParamTuner::load("settings.xml"), 0 ) ;
  EXPECT_EQ( ParamTuner::load("settings.xml"), 0 ) ;
}

TEST(ParamTunerTest, checkInvalidFileLoad_OpenIssue)
{
  EXPECT_EQ( ParamTuner::load("notexistingfile.xml"), -1 ) ;
}

int main(int argc, char **argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}

