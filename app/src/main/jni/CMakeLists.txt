cmake_minimum_required(VERSION 3.10)
project("tulip")
add_library(app SHARED app.cpp)
find_library(log-lib log)
target_link_libraries(app ${log-lib})