@echo off
cls

:: Prompt for username and password
set /p user_name=Type type username for MySQL (root), followed by [ENTER]:
echo You have entered: %user_name%
set /p password=Type type password for MySQL, followed by [ENTER]:

:: Create pt schema and load data
:: Replace \ with \\ for mysql
set subdir=%cd%\csv_test_data\
set path_to_csv=%subdir:\=\\%
:: Format the sql script
setlocal
set "search=[csv_files_path]"
set "replace=%path_to_csv%"
set "textfile=create_silverline_schema.sql"
set "newfile=temp_create_silverline_schema.sql"
(for /f "delims=" %%i in (%textfile%) do (
    set "line=%%i"
    setlocal enabledelayedexpansion
    set "line=!line:%search%=%replace%!"
    echo(!line!
    endlocal
))>"%newfile%"

:: Run the sql script
mysql.exe mysql --default-character-set=utf8 -u%user_name% -p%password% < temp_create_silverline_schema.sql

:: Delete the temp file
del temp_create_silverline_schema.sql

echo Done