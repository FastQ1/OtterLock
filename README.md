# OtterLock
I'd say this was my first non-trivial personal project. The complexity can be demonstrated through this example control flow:

1. User selects a directory to encrypt, and a destination directory to move the files to. The program operates under the assumption that generally, the user's intended result will be moving unencrypted files FROM the source directory TO the destination directory, and the same while decrypting (the program allows for single directory encryption, but this logic is simpler).
1.1 That source directory may contain a mix of encrypted and decrypted files, and the destination directory as well.
1.2 There also may be overlapping directory names, and nested subdirectories.
1.3 There may be overlapping file names

2. The program must first detect if there are any overlapping directories, accept user input to abort the process, overwrite the destination directory, or ignore that specific directory name for the operation.

3. The program must then recognize unexpected state in source and destination directories. Since simply looking at file names is a relatively cheap operation relative to the rewriting the program is expected to do, a preemptive check can be made. It must "remember" the previous user specification regarding overlapping subdirectories when considering which files to alert the user to- it doesn't make sense in this context to alert the user a directory she is going to skip anyways isn't in an expected state.

4. The program must then check files for overlapping names. Since the program adds an extension to mark an encrypted file, it doesn't check for exact matches but what the match will be after the operation is performed.
4.1 The above specification was handled poorly, the program alerts DURING encryption, after previous files may have already been altered. While easy to undo, it would've been more prudent to simply use a map and check preemptively. I anticipate this would be a relativel quick change, however I'm occupied with other projects now and don't anticipate getting back to it.

If I were to do it over, there are a LOT of things I'd do differently. However, I'm pretty proud of this project, especially given how early in my journey I was when I made it.
