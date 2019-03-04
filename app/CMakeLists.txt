cmake_minimum_required(VERSION 3.4.1)

add_library(uart-lib SHARED src/main/cpp/uart-lib.cpp )

find_library(log-lib log )

target_link_libraries(uart-lib ${log-lib} )
