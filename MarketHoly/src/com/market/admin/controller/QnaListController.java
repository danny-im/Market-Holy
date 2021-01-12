package com.market.admin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.market.admin.dao.QnaAdminDao;
import com.market.admin.dto.QnaAdminDto;

@WebServlet("/admin/qnaList.do")
public class QnaListController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final int PAGE_CNT = 10; // 글 목록 개수
		final double PAGE_BLOCK = 10.0; // 페이지 블록
		String spageNum = req.getParameter("pageNum");
		int pageNum = 1;
		if (spageNum != null) {
			pageNum = Integer.parseInt(spageNum);
		}
		int startRow = (pageNum - 1) * PAGE_CNT + 1;
		int endRow = (startRow + PAGE_CNT) - 1;
		
		String kind = req.getParameter("kind");
		if (kind == null) {
			kind = "";
		}
		String word = req.getParameter("word");
		if (word == null) {
			word = "";
		}

		QnaAdminDao dao = QnaAdminDao.getInstance();
		ArrayList<QnaAdminDto> qnaList = dao.selQnaList(startRow, endRow, kind, word);

		int pageCount = (int) Math.ceil(dao.selQnaCount(kind, word) / PAGE_BLOCK);
		int startPageNum = (int) (Math.floor((pageNum - 1) / PAGE_BLOCK) * PAGE_BLOCK + 1);
		int endPageNum = (int) (startPageNum + (PAGE_BLOCK - 1));
		if (pageCount < endPageNum) {
			endPageNum = pageCount;
		}
		
		JSONArray jsonArr = new JSONArray();
		JSONArray jarr = new JSONArray();
		for (QnaAdminDto dto : qnaList) {
			JSONObject json = new JSONObject();
			json.put("qnum", dto.getQnum());
			json.put("pname", dto.getPname());
			json.put("title", dto.getTitle());
			json.put("writer", dto.getName());
			json.put("reg_date", dto.getReg_date());
			json.put("content", dto.getContent());
			json.put("pnum", dto.getPnum());
			json.put("level", dto.getLevel());
			jarr.put(json);
		}

		jsonArr.put(jarr);
		jsonArr.put(startPageNum);
		jsonArr.put(endPageNum);
		jsonArr.put(pageNum);
		jsonArr.put(pageCount);
		resp.setContentType("text/plain;charset=utf-8");
		PrintWriter pw = resp.getWriter();
		pw.print(jsonArr);
	}
}
