cmake_minimum_required(VERSION 2.8)

include(ExternalProject)
find_program(GO go)
if(${GO-NOTFOUND})
	message(FATAL_ERROR "Go not found")
endif()

get_filename_component(SYSROOT "${CMAKE_SOURCE_DIR}/sysroot" REALPATH)
set(PARALLEL "-j8" CACHE STRING "Arguments for make for parallel builds")

set(TARGET "i686-cm-linux")
set(TARGET_DIR "${SYSROOT}/usr")

set(ENABLE_GDB OFF CACHE BOOL "Whether to build gdb or not")

set(BUILD_DEST ${SYSROOT})
set(ENV{BUILD_DEST} /${BUILD_DEST})
set(ENV{KERNEL_VER} linux-2.6.28)
set(ENV{CROSS_COMPILE} ${BUILD_DEST}/usr/bin/i686-cm-linux-)
set(ENV{ARCH} i386)
set(ENV{FSROOT} ${BUILD_DEST})
#message("export CROSS_COMPILE=$ENV{CROSS_COMPILE}")
#message("export KERNEL_VER=$ENV{KERNEL_VER}")
#message("export BUILD_DEST=$ENV{BUILD_DEST}")
#message("export ARCH=$ENV{ARCH}")
set(TARGETCMDS "FSROOT=$ENV{FSROOT}")
set(CMDS addr2line ar as c++ cpp g++ gcc gccbug gcov gprof ld nm objcopy objdump ranlib readelf size strings strip)
foreach(CMD ${CMDS})
	string(TOUPPER TARGET${CMD} t)
	string(REPLACE ++ "XX" t ${t})
	set(ENV{${t}}  $ENV{CROSS_COMPILE}${CMD})
	set(TARGETCMDS ${TARGETCMDS} "${t}=$ENV{${t}}")
	#message("export ${t}=$ENV{${t}}")
	# add_custom_target(
	# 	${CMD}
	# 	COMMAND ${CMAKE_COMMAND} -E create_symlink ${SYSROOT}/usr/bin/i686-pc-linux-gnu-${CMD} $ENV{${t}}
	# 	DEPENDS ${BUILD_DEST}/bin
	# )
endforeach()
set(ENV{TARGETCC} ${BUILD_DEST}/usr/bin/i686-cm-linux-gcc)
set(TARGETCMDS ${TARGETCMDS} TARGETCC=$ENV{TARGETCC})
set(SMD_MAKE EXE_DIR=${TARGET_DIR}/bin ARCH=$$$$$ENV{ARCH} CROSS_COMPILE=$ENV{CROSS_COMPILE} ${TARGETCMDS} BUILD_DEST=$ENV{BUILD_DEST} KERNEL_VER=$ENV{KERNEL_VER} make all)



if(${VERBOSE})
	set(ENV{LOUD} 1)
endif()

include(ExternalProject)

execute_process(COMMAND ${CMAKE_C_COMPILER} -dumpmachine OUTPUT_VARIABLE HOSTMACHINE)
string(REPLACE "\n" "" HOSTMACHINE ${HOSTMACHINE})

ExternalProject_Add(
	kernel-boot
	URL					${CMAKE_SOURCE_DIR}/kernel-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR			kernel-boot
	BINARY_DIR			kernel-boot/IntelCE/kernel-13.11.10484.229643
	PATCH_COMMAND		${CMAKE_COMMAND} -E chdir IntelCE/kernel-13.11.10484.229643/ ${CMAKE_COMMAND} -E tar xfz linux-2.6.28-src.tar.gz
	CONFIGURE_COMMAND	""
	BUILD_COMMAND		make ARCH=i386 CROSS_COMPILE=i686-linux-elf- -C linux-2.6.28 gen3_defconfig
		COMMAND 		make ARCH=i386 CROSS_COMPILE=i686-linux-elf- -C linux-2.6.28 include/linux/version.h
	INSTALL_COMMAND		${CMAKE_COMMAND} -E copy_directory linux-2.6.28/include/linux ${TARGET_DIR}/include/linux
		COMMAND			${CMAKE_COMMAND} -E copy_directory linux-2.6.28/arch/x86/include/asm ${TARGET_DIR}/include/asm
		COMMAND			${CMAKE_COMMAND} -E copy_directory linux-2.6.28/include/asm-generic ${TARGET_DIR}/include/asm-generic
)

ExternalProject_Add(
	boot-binutils
	PREFIX 				${CMAKE_BINARY_DIR}/binutils
	URL 				http://ftp.gnu.org/gnu/binutils/binutils-2.24.tar.bz2
	URL_MD5 			e0f71a7b2ddab0f8612336ac81d9636b
	CONFIGURE_COMMAND	 <SOURCE_DIR>/configure --with-sysroot=${SYSROOT} --with-lib-path=${TARGET_DIR}/lib --prefix=${TARGET_DIR} --target ${TARGET} --disable-nls --disable-werror --program-prefix=${TARGET}-
	BUILD_COMMAND		make ${PARALLEL}
	DEPENDS 			kernel-boot
)

ExternalProject_Add(
	boot-gcc
	PREFIX 				${CMAKE_BINARY_DIR}/gcc
	URL 				ftp://ftp.mirrorservice.org/sites/sourceware.org/pub/gcc/releases/gcc-4.9.2/gcc-4.9.2.tar.bz2
	URL_MD5 			4df8ee253b7f3863ad0b86359cd39c43
	CONFIGURE_COMMAND	<SOURCE_DIR>/configure --with-sysroot=${SYSROOT} --target=${TARGET} --prefix=${TARGET_DIR} --disable-nls --without-headers --with-newlib --enable-languages=c --disable-decimal-float --disable-shared --disable-threads --disable-libmudflap --disable-libssp --disable-libgomp --with-local-prefix=${TARGET_DIR} --with-native-system-header-dir=${TARGET_DIR}/include  --disable-libstdcxx --disable-libquadmath --disable-libatomic #--enable-bootstrap --disable-libquadmath  --disable-libatomic
	BUILD_COMMAND		make ${PARALLEL}
	INSTALL_COMMAND		make install
#		COMMAND ${CMAKE_COMMAND} -E create_symlink ${TARGET_DIR}/lib/gcc/${TARGET}/4.9.2/libgcc.a  ${TARGET_DIR}/lib/gcc/${TARGET}/4.9.2/libgcc_eh.a
	DEPENDS 			boot-binutils
)

ExternalProject_Add(
	glibc
	# glibc 2.20 can't be built for linux 2.6.28
	URL 				http://ftp.gnu.org/gnu/glibc/glibc-2.19.tar.bz2
	URL_MD5 			3ca28f5099257aade61b1adedb7e3401
	# PATCH_COMMAND		cat elf/Makefile | sed s,\ readelf\ ,\ ${PREFIX}/bin/${TARGET}-readelf\ ,g > elf/Makefile.new
	# 	COMMAND 		${CMAKE_COMMAND} -E copy elf/Makefile.new elf/Makefile
	# 	COMMAND 		cat configure | sed s,\ readelf\ ,\ ${TARGET_DIR}/bin/${TARGET}-readelf\ ,g > configure.new
	# 	COMMAND 		${CMAKE_COMMAND} -E copy configure.new configure
	# 	COMMAND 		chmod +x configure
	BUILD_COMMAND		export BUILD_CFLAGS=-I/usr/local/Cellar/gettext/0.19.4/include/ && export BUILD_LDFLAGS=-L/usr/local/Cellar/gettext/0.19.4/lib/\ -lintl && make ${PARALLEL}
	CONFIGURE_COMMAND	PATH=$ENV{PATH}:${TARGET_DIR}/bin/ CC=$ENV{TARGETCC} <SOURCE_DIR>/configure --build=${HOSTMACHINE} --target=${TARGET} --host=${TARGET} --enable-kernel=2.6.28 --prefix=/usr --with-headers=${TARGET_DIR}/include ac_cv_prog_BUILD_CC=/usr/local/bin/gcc-4.9  --program-prefix=${TARGET}-  libc_cv_ssp=no # libc_cv_forced_unwind=yes libc_cv_c_cleanup=yes libc_cv_ctors_header=yes
	INSTALL_COMMAND		make ${PARALLEL} install_root=${SYSROOT} install
	DEPENDS boot-gcc
)

ExternalProject_Add(
	gcc2
	PREFIX 				${CMAKE_BINARY_DIR}/gcc
	URL 				ftp://ftp.mirrorservice.org/sites/sourceware.org/pub/gcc/releases/gcc-4.9.2/gcc-4.9.2.tar.bz2
	URL_MD5 			4df8ee253b7f3863ad0b86359cd39c43
	CONFIGURE_COMMAND	ac_cv_header_sys_filio_h=no <SOURCE_DIR>/configure --with-sysroot=${SYSROOT} --target=${TARGET} --prefix=${TARGET_DIR} --disable-libstdcxx-pch --enable-clocale=gnu --enable-shared --enable-threads --enable-languages=c,c++ --disable-nls --disable-multilib --disable-libgomp --disable-libmudflap --disable-libssp --disable-bootstrap --with-local-prefix=${TARGET_DIR} --with-native-system-header-dir=/usr/include --program-prefix=${TARGET}-
	BUILD_COMMAND 		ac_cv_header_sys_filio_h=no make ${PARALLEL}
	DEPENDS 			glibc
)

if(ENABLE_GDB)
ExternalProject_Add(
	gdb
	PREFIX 				${CMAKE_BINARY_DIR}/gdb
	URL 				http://ftp.gnu.org/gnu/gdb/gdb-6.8a.tar.bz2
	URL_MD5				da20d043e6729f74b909bd507bcae5c9
	CONFIGURE_COMMAND 	<SOURCE_DIR>/configure --with-sysroot=${SYSROOT} --target=${TARGET} --prefix=${TARGET_DIR} --program-prefix=${TARGET}- --disable-werror
	BUILD_COMMAND 		make ${PARALLEL}
	DEPENDS 			gcc2
)

ExternalProject_Add(
	termcap
	URL 				ftp://ftp.gnu.org/gnu/termcap/termcap-1.3.1.tar.gz
	URL_MD5 			ffe6f86e63a3a29fa53ac645faaabdfa
	CONFIGURE_COMMAND 	CC=${TARGET}-gcc RANLIB=${TARGET}-ranlib <SOURCE_DIR>/configure --target=${TARGET} --prefix=${SYSROOT}/usr/local/
	BUILD_COMMAND 		make ${PARALLEL}
	DEPENDS 			gcc2
)

ExternalProject_Add(
	targetgdb
	PREFIX 				${CMAKE_BINARY_DIR}/gdb
	URL 				http://ftp.gnu.org/gnu/gdb/gdb-6.8a.tar.bz2
	URL_MD5				da20d043e6729f74b909bd507bcae5c9
	CONFIGURE_COMMAND 	LDFLAGS=-L${SYSROOT}/usr/local/lib <SOURCE_DIR>/configure --host=${TARGET} --prefix=${SYSROOT}/usr/local/ --disable-werror
	BUILD_COMMAND 		make ${PARALLEL}
	DEPENDS 			gcc2 termcap
)
endif()

file(WRITE "${SYSROOT}/usr/bin/${TARGET}-pkg-config" "
#!/bin/bash

set -e
export PKG_CONFIG_DIR=
export PKG_CONFIG_LIBDIR=${SYSROOT}/usr/lib/pkgconfig:${SYSROOT}/usr/share/pkgconfig:${SYSROOT}/usr/local/lib/pkgconfig:${SYSROOT}/usr/local/share/pkgconfig
export PKG_CONFIG_SYSROOT_DIR=${SYSROOT}

exec pkg-config \"\$@\" | python -c \"import sys; print sys.stdin.read().replace('${SYSROOT}${SYSROOT}', '${SYSROOT}'),\" && test \${PIPESTATUS[0]} -eq 0
")
execute_process(COMMAND chmod +x ${SYSROOT}/usr/bin/${TARGET}-pkg-config)



# add_custom_command(
# 	OUTPUT ${BUILD_DEST}/bin
# 	COMMAND ${CMAKE_COMMAND} -E make_directory ${BUILD_DEST}/bin
# )

#message("SMD_MAKE = ${SMD_MAKE}")

add_custom_target(
	toolchain
	DEPENDS gcc2 glibc
)

add_custom_target(
	audio_fw
	COMMAND ${GO} run ./extract_audio_fw.go
	WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
	DEPENDS ${CMAKE_SOURCE_DIR}/extract_audio_fw.go
)

ExternalProject_Add(
	smd_common
	URL 				${CMAKE_SOURCE_DIR}/SMD_Common-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			SMD_Common
	BINARY_DIR 			SMD_Common/IntelCE/SMD_Common-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS toolchain
)



ExternalProject_Add(
	kernel
	URL 				${CMAKE_SOURCE_DIR}/kernel-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR			kernel
	BINARY_DIR			ffff=${f}kernel/In$telCE/k$er$$${RR}nel-13.11.10484.229643
	PATCH_COMMAND
		COMMAND ${CMAKE_COMMAND} -E chdir IntelCE/kernel-13.11.10484.229643/ ${CMAKE_COMMAND} -E tar xvfz linux-2.6.28-src.tar.gz
		COMMAND ${CMAKE_COMMAND} -E create_symlink /usr/include/malloc/malloc.h IntelCE/kernel-13.11.10484.229643/linux-2.6.28/scripts/genksyms/malloc.h
		COMMAND ${CMAKE_COMMAND} -E copy ${SYSROOT}/usr/include/elf.h IntelCE/kernel-13.11.10484.229643/linux-2.6.28/scripts/mod/elf.h
		COMMAND ${CMAKE_COMMAND} -E copy xxv/vv${SYSROOT}/usr/include/sys/sysmacros.h IntelCE/kernel-13.11.10484.229643/linux-2.6.28/arch/x86/boot/tools/sysmacros.h
		COMMAND sed -i -e "s/#include\ <features.h>//" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/scripts/mod/elf.h
		COMMAND sed -i -e "s/#include\ <features.h>//" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/arch/x86/boot/tools/sysmacros.h
		COMMAND sed -i -e "s,<sys/sysmacros.h>,\"sysmacros.h\"," IntelCE/kernel-13.11.10484.229643/linux-2.6.28/arch/x86/boot/tools/build.c
		COMMAND sed -i -e "s/<elf.h>/\"elf.h\"/" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/scripts/mod/mk_elfconfig.c
		COMMAND sed -i -e "s/<elf.h>/\"elf.h\"/" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/scripts/mod/modpost.h
		COMMAND sed -i -e "s/-m elf_i386//" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/arch/x86/vdso/Makefile
		COMMAND sed -i -e "62s/static void noinline/void/" -e "99s/static noinline void/void/" -e "271s/static noinline void/void/" -e "316s/static noinline void/void/" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/kernel/mutex.c
		COMMAND sed -i -e "s/__THROW __attribute_const__//" -e "37,+2cunsigned int gnu_dev_major(unsigned long long int __dev)" -e "44,+1cunsigned int gnu_dev_minor(unsigned long long int __dev)" -e "50,+1cunsigned long long int gnu_dev_makedev(unsigned int __major, unsigned int __minor)" -e "57d" IntelCE/kernel-13.11.10484.229643/linux-2.6.28/arch/x86/boot/tools/sysmacros.h
#		COMMAND sed -i -E "s,make -C,make -j8 -C," IntelCE/kernel-13.11.10484.229643/build_all.sh
		COMMAND sed -E "s,asmregparm,," -i IntelCE/kernel-13.11.10484.229643/linux-2.6.28/arch/x86/kernel/ptrace.c
	CONFIGURE_COMMAND	""
	INSTALL_COMMAND		${CMAKE_COMMAND} -E create_symlink ${BUILD_DEST}/kernel/linux-2.6.28/include/linux ${BUILD_DEST}/kernel/linux-2.6.28/include/generated
		COMMAND			${CMAKE_COMMAND} -E copy ${CMAKE_SOURCE_DIR}/pci-intelce_pm.h ${BUILD_DEST}/kernel/linux-2.6.28/include/linux
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS 			toolchain
)

# ExternalProject_Add_Step(
# 	kernel
# 	hg_kernel
# 	DEPENDEES download
# 	DEPENDERS patch
# 	COMMAND hg clone https://code.google.com/p/googletv-mirrored-source.kernel <BINARY_DIR>/linux-2.6.35
# )

ExternalProject_Add(
	osal
	URL 				${CMAKE_SOURCE_DIR}/osal-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR			osal
	BINARY_DIR			osal/IntelCE/osal-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS 			smd_common kernel
)

ExternalProject_Add(
	auto_eas
	URL 				${CMAKE_SOURCE_DIR}/auto_eas-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			auto_eas
	BINARY_DIR 			auto_eas/IntelCE/auto_eas-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS osal
)

ExternalProject_Add(
	htuple
	URL 				${CMAKE_SOURCE_DIR}/htuple-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			htuple
	BINARY_DIR 			htuple/IntelCE/htuple-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS kernel smd_common
)

ExternalProject_Add(
	platform_config
	URL 				${CMAKE_SOURCE_DIR}/platform_config-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			platform_config
	BINARY_DIR 			platform_config/IntelCE/platform_config-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS htuple
)

ExternalProject_Add(
	pal
	URL					${CMAKE_SOURCE_DIR}/pal-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			pal
	BINARY_DIR 			pal/IntelCE/pal-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS auto_eas platform_config
)

ExternalProject_Add(
	sven
	URL 				${CMAKE_SOURCE_DIR}/sven-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			sven
	BINARY_DIR 			sven/IntelCE/sven-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS auto_eas platform_config pal
)

ExternalProject_Add(
	api
	URL 				${CMAKE_SOURCE_DIR}/api-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			api
	BINARY_DIR 			api/IntelCE/api-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS osal sven
)

ExternalProject_Add(
	smd_tools
	URL 				${CMAKE_SOURCE_DIR}/smd_tools-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			smd_tools
	BINARY_DIR 			smd_tools/IntelCE/smd_tools-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS osal sven
)

ExternalProject_Add(
	idl
	URL 				${CMAKE_SOURCE_DIR}/idl-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			idl
	BINARY_DIR 			idl/IntelCE/idl-13.11.10484.229643
	PATCH_COMMAND		sed -i -e "128d" IntelCE/idl-13.11.10484.229643/idl/Makefile.inc
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE} install
	DEPENDS osal sven
)

ExternalProject_Add(
	clock_control
	URL 				${CMAKE_SOURCE_DIR}/clock_control-DUAL-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			clock_control
	BINARY_DIR 			clock_control/IntelCE/clock_control-13.11.10484.229643
	PATCH_COMMAND
		COMMAND         sed -i -e "s/-pd//" IntelCE/clock_control-13.11.10484.229643/src/kernel/Makefile
		COMMAND         sed -i -e "174s/-Werror//" IntelCE/clock_control-13.11.10484.229643/Makefile.include
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS idl
)

ExternalProject_Add(
	clock
	URL 				${CMAKE_SOURCE_DIR}/clock-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			clock
	BINARY_DIR 			clock/IntelCE/clock-13.11.10484.229643
	PATCH_COMMAND		sed -i -e "55s/clock_control//" -e "s/-Werror//" IntelCE/clock-13.11.10484.229643/src/src/Makefile
		COMMAND			sed -i -e "s/-Werror//" IntelCE/clock-13.11.10484.229643/hal/hal/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS osal sven clock_control
)



ExternalProject_Add(
	intel_ce_pm
	URL 				${CMAKE_SOURCE_DIR}/intel_ce_pm-DUAL-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			intel_ce_pm
	BINARY_DIR 			intel_ce_pm/IntelCE/intel_ce_pm-13.11.10484.229643
	PATCH_COMMAND		sed -i -e "63s/user//" -e "130s/doc//" IntelCE/intel_ce_pm-13.11.10484.229643/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/intel_ce_pm-13.11.10484.229643/kernel/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/intel_ce_pm-13.11.10484.229643/kernel/lib/Makefile
		COMMAND         sed -i -e "162s/-Werror//" IntelCE/intel_ce_pm-13.11.10484.229643/Makefile.include
		COMMAND         ${CMAKE_COMMAND} -E copy ${CMAKE_SOURCE_DIR}/init_intel_ce_pm IntelCE/intel_ce_pm-13.11.10484.229643/init_intel_ce_pm
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS clock_control
)

ExternalProject_Add(
	system_utils
	URL 				${CMAKE_SOURCE_DIR}/system_utils-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			system_utils
	BINARY_DIR 			system_utils/IntelCE/system_utils-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS osal sven
)

ExternalProject_Add(
	core
	URL 				${CMAKE_SOURCE_DIR}/core-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			core
	BINARY_DIR 			core/IntelCE/core-13.11.10484.229643
	PATCH_COMMAND		sed -i -e "600s/from_tag_storage,/from_tag_storage=0,/" IntelCE/core-13.11.10484.229643/core/ismd_buffer_tags.c
		COMMAND         sed -i -e "s/-Werror//" IntelCE/core-13.11.10484.229643/core/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS osal sven api smd_tools intel_ce_pm system_utils
)

ExternalProject_Add(
	nandflash
	URL 				${CMAKE_SOURCE_DIR}/nandflash-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			nandflash
	BINARY_DIR 			nandflash/IntelCE/nandflash-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		PATH=$ENV{PATH}:${BUILD_DEST}/usr/bin ${SMD_MAKE}
	DEPENDS kernel
)

# ExternalProject_Add(
# 	generic_timer
# 	URL 				${CMAKE_SOURCE_DIR}/generic_timer.tar.gz
# 	SOURCE_DIR 			generic_timer
# 	BINARY_DIR 			generic_timer/generic_timer
# 	CONFIGURE_COMMAND 	""
# 	INSTALL_COMMAND		""
# 	BUILD_COMMAND		${SMD_MAKE}
# 	DEPENDS kernel smd_tools
# )

ExternalProject_Add(
	display
	URL 				${CMAKE_SOURCE_DIR}/display-DUAL-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			display
	BINARY_DIR 			display/IntelCE/display-13.11.10504.232647
	PATCH_COMMAND		${CMAKE_COMMAND} -E copy ${CMAKE_SOURCE_DIR}/gdl.h IntelCE/display-13.11.10504.232647/src/include
		COMMAND			${CMAKE_COMMAND} -E copy ${CMAKE_SOURCE_DIR}/libgdl.h IntelCE/display-13.11.10504.232647/src/include
		COMMAND			sed -i -e "s/-pd//" IntelCE/display-13.11.10504.232647/src/lib/libvbd/Makefile
		COMMAND			sed -i -e "s/-pd//" IntelCE/display-13.11.10504.232647/src/lib/libabd/Makefile
		COMMAND			sed -i -e "s/-pd//" IntelCE/display-13.11.10504.232647/src/kernel/Makefile
		COMMAND			sed -i -e "s/-Werror//" IntelCE/display-13.11.10504.232647/Makefile.include
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS kernel idl system_utils intel_ce_pm
)



ExternalProject_Add(
	audio
	URL 				${CMAKE_SOURCE_DIR}/audio-None-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			audio
	BINARY_DIR 			audio/IntelCE/audio-13.11.10504.232647
	PATCH_COMMAND		sed -i -e "94a ifneq ($(strip $$(have_aud_inc)),)" -e "95a endif" IntelCE/audio-13.11.10504.232647/Makefile
		COMMAND 		sed -i -e "295s/inline//" IntelCE/audio-13.11.10504.232647/core/audio_core.h
		COMMAND			sed -i -e "s/-Werror//" IntelCE/audio-13.11.10504.232647/core/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		CORE_A_NAME=dsp0 CORE_B_NAME=dsp1 ${SMD_MAKE}
	DEPENDS core display audio_fw
)

# ExternalProject_Add(
# 	alsa_shim
# 	URL 				${CMAKE_SOURCE_DIR}/alsa_shim-None-SRC-13.11.10504.232647.tgz
# 	SOURCE_DIR 			alsa_shim
# 	BINARY_DIR 			alsa_shim/IntelCE/alsa_shim-13.11.10504.232647
# 	CONFIGURE_COMMAND 	""
# 	INSTALL_COMMAND		""
# 	BUILD_COMMAND		KERNEL_BUILD_DIR=${BUILD_DEST}/kernel/linux-2.6.28 ${SMD_MAKE}
# 	DEPENDS audio
# )

ExternalProject_Add(
	edl
	URL 				${CMAKE_SOURCE_DIR}/edl-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			edl
	BINARY_DIR 			edl/IntelCE/edl-13.11.10504.232647
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		PATH=$ENV{PATH}:${BUILD_DEST}/usr/bin  ${SMD_MAKE}
	DEPENDS audio
)


ExternalProject_Add(
	clock_recovery
	URL 				${CMAKE_SOURCE_DIR}/clock_recovery-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			clock_recovery
	BINARY_DIR 			clock_recovery/IntelCE/clock_recovery-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)
ExternalProject_Add(
	demux
	URL 				${CMAKE_SOURCE_DIR}/demux-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			demux
	BINARY_DIR 			demux/IntelCE/demux-13.11.10484.229643
	PATCH_COMMAND		sed -i -E "1251s/discon_detection_enabled\\s/discon_detection_enabled[stream_id]/" IntelCE/demux-13.11.10484.229643/ver3/driver/ismd_demux_tags_timing.c
		COMMAND			sed -i -e "4692s/$<SEMICOLON>/=odd_or_even$<SEMICOLON>/" IntelCE/demux-13.11.10484.229643/ver3/driver/ismd_demux_api.c
		COMMAND			sed -i -e "102s/clock_control//" -e "s/-Werror//" IntelCE/demux-13.11.10484.229643/ver3/driver/Makefile
		COMMAND			sed -i -e "52s/clock_control//" -e "s/-Werror//" IntelCE/demux-13.11.10484.229643/ver2/hal/tsi/Makefile
		COMMAND			sed -i -e "115s/clock_control//" -e "s/-Werror//" IntelCE/demux-13.11.10484.229643/ver2/core/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core clock_recovery
)

ExternalProject_Add(
	viddec_fw
	URL 				${CMAKE_SOURCE_DIR}/viddec_fw-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			viddec_fw
	BINARY_DIR 			viddec_fw/IntelCE/viddec_fw-13.11.10504.232647
	PATCH_COMMAND 		sed -i -e "s/-Werror//" IntelCE/viddec_fw-13.11.10504.232647/host/decoder/Makefile
		COMMAND			sed -i -e "s/-Werror//" IntelCE/viddec_fw-13.11.10504.232647/host/parser/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)

ExternalProject_Add(
	viddec
	URL 				${CMAKE_SOURCE_DIR}/viddec-DUAL-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			viddec
	BINARY_DIR 			viddec/IntelCE/viddec-13.11.10504.232647
	PATCH_COMMAND		sed -i -e "s/-Werror//" IntelCE/viddec-13.11.10504.232647/hal/Makefile
		COMMAND 		sed -i -e "s/-Werror//" IntelCE/viddec-13.11.10504.232647/src/Makefile
		COMMAND 		sed -i -e "s/-Werror//" IntelCE/viddec-13.11.10504.232647/version2/hal/gen4/Makefile
		COMMAND 		sed -i -e "s/-Werror//" IntelCE/viddec-13.11.10504.232647/version2/src/Makefile
	# PATCH_COMMAND		sed -i -e "242d" -e "252d" -e "264,268d" IntelCE/viddec-13.11.10504.232647/src/ismd_viddec_pvt.h
	# 	COMMAND 		sed -i -e "238d" -e "248d" -e "260,265d" IntelCE/viddec-13.11.10504.232647/version2/src/ismd_viddec_pvt.h
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS viddec_fw
)

ExternalProject_Add(
	common
	URL 				${CMAKE_SOURCE_DIR}/common-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			common
	BINARY_DIR 			common/IntelCE/common-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)

ExternalProject_Add(
	ipclib
	URL 				${CMAKE_SOURCE_DIR}/ipclib-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			ipclib
	BINARY_DIR 			ipclib/IntelCE/ipclib-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)

ExternalProject_Add(
	vidpproc_fw
	URL 				${CMAKE_SOURCE_DIR}/vidpproc_fw-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			vidpproc_fw
	BINARY_DIR 			vidpproc_fw/IntelCE/vidpproc_fw-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core ipclib
)

ExternalProject_Add(
	vidpproc
	URL 				${CMAKE_SOURCE_DIR}/vidpproc-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			vidpproc
	BINARY_DIR 			vidpproc/IntelCE/vidpproc-13.11.10484.229643
	PATCH_COMMAND 		sed -i -e "s/-Werror//" IntelCE/vidpproc-13.11.10484.229643/core/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS vidpproc_fw common
)

ExternalProject_Add(
	vidrend
	URL 				${CMAKE_SOURCE_DIR}/vidrend-None-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			vidrend
	BINARY_DIR 			vidrend/IntelCE/vidrend-13.11.10504.232647
	PATCH_COMMAND 		sed -i -e "s/-Werror//" IntelCE/vidrend-13.11.10504.232647/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)

ExternalProject_Add(
	tsout
	URL 				${CMAKE_SOURCE_DIR}/tsout-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			tsout
	BINARY_DIR 			tsout/IntelCE/tsout-13.11.10484.229643
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)

ExternalProject_Add(
	avcap
	URL 				${CMAKE_SOURCE_DIR}/avcap-DUAL-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			avcap
	BINARY_DIR 			avcap/IntelCE/avcap-13.11.10504.232647
	PATCH_COMMAND		sed -i -e "62a #include <linux/slab.h>" IntelCE/avcap-13.11.10504.232647/src/kernel/core/avcap_core_queue.c
		COMMAND			sed -i -e "61a #include <linux/slab.h>" IntelCE/avcap-13.11.10504.232647/src/kernel/native_synthetic/synth_render.c
		COMMAND			sed -i -e "69d" IntelCE/avcap-13.11.10504.232647/Makefile
		COMMAND			sed -i -e "s/-Werror//" IntelCE/avcap-13.11.10504.232647/Makefile.include
		COMMAND         sed -i -e "s/-pd//" IntelCE/avcap-13.11.10504.232647/src/lib/libvidcap_user/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/avcap-13.11.10504.232647/src/lib/libvidcap_kernel/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/avcap-13.11.10504.232647/src/kernel/core/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/avcap-13.11.10504.232647/src/kernel/native_synthetic/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/avcap-13.11.10504.232647/src/repeater/platform/custom/Makefile
		COMMAND         sed -i -e "s/-pd//" IntelCE/avcap-13.11.10504.232647/src/repeater/platform/internal/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core
)


ExternalProject_Add(
	bufmon
	URL 				${CMAKE_SOURCE_DIR}/bufmon-None-SRC-13.11.10484.229643.tgz
	SOURCE_DIR 			bufmon
	BINARY_DIR 			bufmon/IntelCE/bufmon-13.11.10484.229643
	PATCH_COMMAND		sed -i -e "s/-Werror//" IntelCE/bufmon-13.11.10484.229643/Makefile
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		${SMD_MAKE}
	DEPENDS core clock_recovery
)

ExternalProject_Add(
	graphics
	URL 				${CMAKE_SOURCE_DIR}/graphics-GPL-SRC-13.11.10504.232647.tgz
	SOURCE_DIR 			graphics
	BINARY_DIR 			graphics/eurasia_km
	PATCH_COMMAND		${CMAKE_COMMAND} -E tar xvfz IntelCE/graphics-13.11.10504.232647/src/gfx_km_src.tgz
		COMMAND			sed -i -E "s,SUPPORT_DRI_DRM = 1,SUPPORT_DRI_DRM = 0," eurasia_km/eurasiacon/build/linux/sgx_intel_ce/makefile.shared_conf
	CONFIGURE_COMMAND 	""
	INSTALL_COMMAND		""
	BUILD_COMMAND		INTEL_D3_CHANGES=1 INTEL_D3_PAD=1 INTEL_D3_NO_PCI_ENUM=1 INTEL_D3_DISABLE_TEXTURE_STREAM=1 INTEL_D3_P_CHANGES=1 INTEL_D3_CACHED_CBUF=1 INTEL_D3_FLUSH=1 INTEL_D3_PM=1 INSTALL_FSROOT=${BUILD_DEST} SUPPORT_SGX=1 DISP_ROOT=${CMAKE_BINARY_DIR}/display/IntelCE/display-13.11.10504.232647 GFX_ROOT=${CMAKE_BINARY_DIR}/display/IntelCE/display-13.11.10504.232647 ${SMD_MAKE}
	DEPENDS kernel osal idl intel_ce_pm
)

# ExternalProject_Add(
# 	sec
# 	URL 				${CMAKE_SOURCE_DIR}/sec-DUAL-SRC-13.11.10504.232647.tgz
# 	SOURCE_DIR 			sec
# 	BINARY_DIR 			sec/IntelCE/sec-13.11.10504.232647
# #	PATCH_COMMAND		${CMAKE_COMMAND} -E copy ${CMAKE_SOURCE_DIR}/sec_types.h sec/kernel/sec_types.h
# 	CONFIGURE_COMMAND 	""
# 	INSTALL_COMMAND		""
# 	BUILD_COMMAND		${SMD_MAKE}
# 	DEPENDS kernel
# )



