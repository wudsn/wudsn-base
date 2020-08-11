This is a list of all known cartrdge images for the Atari 8-bit computers. The goal is to:
- verify correctness of all known ROM images, by obtaining at least two dumps of independent origin for each entry,
- analyse other known cartridge images and correctly label them as overdumps, bad images, cracks etc.,
- list all known cartridge releases for the Atari 8-bits, regardless of availability of their ROM images.

=== Conventions ===
Each ROM image is identified by its CRC-32 checksum, in the "CRC-32" column. If a checksum is not given, then it means a ROM image has not yet been found.

The "Comments" column can contain several "tags" in square brackets, along with additional descriptions in prose. The tags are reminiscent of the ones used in the TOSEC Naming Convention. If a tag contains a question mark, then it means I am not sure of it.

==== The [!] tag ====

A ROM image is considered verified if at least two different physical cartridges have been dumped and both resulting ROM images are identical. Such ROM image is marked with "[!]" in the Comments column.

A prototype ROM image can be marked with [!] only if there exist at least two physical prototype cartridges which have been dumped and the dumps are identical - ie. reproductions sold by Video 61 don't count in ROM verification. Note that no pair of identical prototype cartridges has been found yet, so no prototype ROM image has been maked with [!].

Homebrew ROMs that were published by their authors on the Internet - so they are not dumps of physical cartridges, but actual binaries from which cartridges are produced - are verified by definition.

Sometimes ROM images are published in CART format (with additional 16-byte header). Such images are placed on the list only after removal of the CART header. (Therefore a ROM image can still be marked as verified, even if only two ROM dumps are known - one raw binary, the other in CART format.)

In a few cases, a ROM image is considered verified even if the known ROM dumps are not identical. Such cases are:
- there exist two overdump images, but each is different in the overdump part;
- there exist two images, each dumped using one of two OSS mapping schemes, ie. 034M and 043M.
In both cases, only the parts of the ROM image that contain real data are compared. If these parts are identical, then the images are considered verified.

==== The [m] tag ====

A ROM image B is considered a modification of another image A, if the side-by-side comparison of images shows only minor differences (such as, a few bytes changes). Such image was probably created by patching the original image A, or it is an incorrect dump of the cartridge A. Such image B is then marked with [m <crc>], where <crc> is CRC-32 checksum of image A.

When analysis of the differences points to reasons of the modification, additional description is given in prose. There are 5 most common modification types - those are marked with an additional tag. Those are:
- [cr] - an image is a crack, ie. a modification that disables copy-protection;
- [f] - an image contains a fix for a bug;
- [h] - an image was modified in some other way, such as changing the version number, changing the text on the credits screen, or zeroing unmapped areas (as in ROM images from k1w1's collection);
- [o] - an image is an overdump, ie. it contains too much data compared to the original;
- [u] - an image is an underdump, ie. it contains only part of a full working image;
- [b] - an image is a bad dump, ie. it was dumped incorrectly and contains invalid parts.

==== The [a] tag ====

A ROM image B is considered an alternate version of another image A, if both are different versions of the same title, and the differences are not simple byte changes as in [m], but significant structural changes. Such alternate versions are probably different releases of the same title, containing bugfixes or other changes. Such image B is then marked with [a <crc>].

==== Tag usage examples ====

[m 3614d0aa] $00s in first 4KB
This image differs from the dump with CRC=3614d0aa, in that it has first 4KB filled with zeroes, but otherwise it's identical.

[m 3614d0aa][cr] $00s in first 4KB
This image differs from the dump with CRC=3614d0aa, in that it has copy-protection removed, and has first 4KB filled with zeroes, but otherwise It's identical.

[m 3614d0aa][o]
The image differs from the dump with CRC=3614d0aa, in that it is an overdump. Otherwise there are no other differences.

=== Additional notes ===

==== k1w1's ROM collection ====

Some ROM imags in the list are taken from k1w1's collection. Initially k1w1 hacked all dumps that had unmapped parts – in place of FF's in the original images, the dumps in k1w1's collection had 00's. Such rom images are marked with "First 4KB zeroed by k1w1" or similar comment. (Note that k1w1's ROMs were not dumped by him, so they are not counted in verification.)

==== Checksum Verifier program by Atari, Inc. ====

There was a program in circulation inside Atari, Inc., that was used to verify correctness or ROM cartridges. The program, named Checksum Verifier, was written by Atari employee John Hinman and contained a list of cartridge names along with their checksums. This program is a useful resource of information on Atari-manufactured cartridges up until 1983-03-18 (the date in which the last version, 4.7, of the software was released).

Unfortunately, the only version of the Checksum Verifier available today is Rev. 5.1, modified by one Glen S. Bruner in 1993-1995. This version contains many more cartridges in the list, and it is hard to ascertain which of the entries are legitimate, ie. added in or before 1983 by Hinman, or illegitmate, ie. added later and based on possibly hacked ROM images. Based on lack of alphabetical order in the "Cartridge Checksums" table below "VIDEO EASEL", it appears that entries below were added later, so they are _not_ legitimate. Above "VIDEO EASEL" there are also entries for cartridges released after 1983-03, and cartidges not produced by Atari, so it's probable that the whole list had been tampered with after version 4.7. Based on the comments, the last entries added by Hinman are Microsoft BASIC II and AtariWriter rev. B.

Rev. 5.1 of Checksum Verifier is available at http://ftp.pigwa.net/stuff/collections/nir_dary_cds/ROMS/ROMS_OS/MISC/

=== Links ===

The newest version of this list is published periodically on the AtariAge forums, in the thread: http://www.atariage.com/forums/topic/161828-hooked-on-8-bit-carts/

For description of various cartridge mapping schemes, see https://github.com/atari800/atari800/blob/master/DOC/cart.txt

