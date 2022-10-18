--hydrusIP<={127.0.0.1} 
--hydrusPort<={45869} 
--hydrusAPIKey<={API Key}
--deepDanBooruWorkingDir<="{C:\DeepDanbooru}" #Path to DeepDanbooru folder
--deepDanBooruEXEPathWithinWorkingDir<="{venv\Scripts\deepdanbooru.exe}" #path to exe relative to the working directory 
--deepDanBooruProjectDirWithinWorkingDir<="{deepdanbooru\project}" #path to the projectfolder 
--tempImageFolder<="{tmpImageFolder}" #path to the temporary image folder 
--hydrusFilterTags<="{\"-special:taggedbydeepdanbooru\",\"system:filetype_=_image/jpg,_image/jpeg,_image/png,_apng\",\"system:limit=3\"}" #Filter to be passed through to Hydrus, System:limit defines the batch size 
--hydrusTagServiceName<="{my tags}" #Name of the Tagging service within Hydrus

A tag will be added called "special:taggedbydeepdanbooru" to enable simple perpetual batch processing.
If there is a wish for a different tag it can be changed within "ImageTags.java"

The assumption this software makes is that there is a fully functional DeepDanbooru https://github.com/KichangKim/DeepDanbooru

There is no input validation so don't make your temporary image folder system32

The Client API has to be setup with a proper set of permissions to allow the fetching of files and their data and the subsquent tagging of files.

